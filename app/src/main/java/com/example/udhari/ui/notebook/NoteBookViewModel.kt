package com.example.udhari.ui.notebook

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.udhari.data.PreferenceDataStore
import com.example.udhari.data.entity.NoteBookEntity
import com.example.udhari.data.repositories.FinanceRepository
import com.example.udhari.navigation.Routes
import com.example.udhari.ui.base.BaseViewModel
import com.example.udhari.utils.SpeechRecognitionHelper
import com.example.udhari.utils.ToastManager
import com.example.udhari.utils.elseCaseHandling
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteBookViewModel @Inject constructor(
    private val repository: FinanceRepository,
    speechRecognitionHelper: SpeechRecognitionHelper ) : BaseViewModel(speechRecognitionHelper) {

    private val _uiState = MutableStateFlow(NoteBookUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: NoteBookEvent) {
        when (event) {
            is NoteBookEvent.FetchNoteBooks -> fetchNoteBooks()
            is NoteBookEvent.NavigateToNoteBook -> navigateToNoteBook(
                event.noteBookId,
                event.globalNavController
            )

            is NoteBookEvent.DeleteNoteBooks -> deleteNoteBooks(event.noteBooks)
            NoteBookEvent.StartVoiceRecognition -> startVoiceRecognition()
            NoteBookEvent.StopVoiceRecognition -> stopVoiceRecognition()
            is NoteBookEvent.SetGlobalNavController -> setGlobalNavController(event.navHostController)
            is NoteBookEvent.AddNoteBookByVoice -> addNotebook(event.noteBookName)
            is NoteBookEvent.DeleteNoteBookByVoice -> deleteNotebookByName(event.noteBookName)
            is NoteBookEvent.OpenNoteBookByVoice -> voiceNavigation(event.noteBookName)
            NoteBookEvent.OpenNoteBookForm -> {
                _uiState.value.globalNavController?.navigate(Routes.AddingNoteBook.route)
            }
        }
    }

    private fun navigateToNoteBook(
        noteBookId: Int,
        globalNavController: NavHostController
    ) {
        viewModelScope.launch {
            globalNavController.navigate("noteBookDetails/${noteBookId}")
        }
    }

    private fun setGlobalNavController(navController: NavHostController) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(globalNavController = navController)
        }
    }


    private fun fetchNoteBooks() {
        viewModelScope.launch {
            val noteBooks = repository.getAllNotebooksWithTotals()
            if (noteBooks.isEmpty()) {
                _uiState.update { currentState ->
                    currentState.copy(isListEmpty = true)
                }
            } else {
                _uiState.update { currentState ->
                    currentState.copy(isListEmpty = false)
                }
            }
            _uiState.update { currentState ->
                currentState.copy(listOfNoteBook = noteBooks)

            }
        }
    }

    private fun deleteNoteBooks(noteBooks: List<Int>) {
        viewModelScope.launch {
            try {
                val noteBooksList = noteBooks.toList()
                repository.deleteNoteBooks(noteBooksList)
                ToastManager.showToast(
                    "Notebooks deleted successfully",
                    isSuccess = true
                )
                fetchNoteBooks()
            } catch (e: Exception) {
                ToastManager.showToast("Error in deleting notebooks", isSuccess = false)
                Log.e("NoteBookViewModel", "deleteNoteBooks: ", e)
            }

        }
    }

    override fun handleVoiceCommand(command: String) {
        when {
            command.contains("add notebook", ignoreCase = true) -> {
                Log.e("handleVoiceCommand", "adding notebook")
                val notebookName = extractNameFromCommand(command)
                onEvent(NoteBookEvent.AddNoteBookByVoice(notebookName.lowercase()))
                onEvent(NoteBookEvent.StopVoiceRecognition)
            }

            command.contains("delete notebook", ignoreCase = true) -> {
                Log.e("handleVoiceCommand", "delete notebook")
                val notebookName = extractNameFromCommand(command)
                onEvent(NoteBookEvent.DeleteNoteBookByVoice(notebookName.lowercase()))
                onEvent(NoteBookEvent.StopVoiceRecognition)
            }

            command.contains("open notebook", ignoreCase = true) -> {
                Log.e("handleVoiceCommand", "open notebook")
                val notebookName = extractNameFromCommand(command)
                onEvent(NoteBookEvent.OpenNoteBookByVoice(notebookName.lowercase()))
                onEvent(NoteBookEvent.StopVoiceRecognition)
            }

            command.contains("go to notebook form", ignoreCase = true) -> {
                Log.e("handleVoiceCommand", "notebook form")
                onEvent(NoteBookEvent.OpenNoteBookForm)
                onEvent(NoteBookEvent.StopVoiceRecognition)
            }

            command.contains("back", ignoreCase = true) -> {
                Log.e("handleVoiceCommand", "back")
                _uiState.value.globalNavController?.popBackStack()
                onEvent(NoteBookEvent.StopVoiceRecognition)
            }

            else -> {
                Log.e("handleVoiceCommand", "else")
                elseCaseHandling("Command not recognized") {
                    onEvent(NoteBookEvent.StopVoiceRecognition)
                }
            }
        }
    }


    override fun startVoiceRecognition() {
        super.startVoiceRecognition()
        _uiState.value = _uiState.value.copy(isVoiceRecognitionStart = true)
    }

    override fun stopVoiceRecognition() {
        super.stopVoiceRecognition()
        _uiState.value = _uiState.value.copy(isVoiceRecognitionStart = false)
    }

    private fun extractNameFromCommand(command: String): String {
        return command.substringAfter("notebook").trim()
    }

    private fun addNotebook(name: String) {
        viewModelScope.launch {
            try {
                repository.insertNotebook(NoteBookEntity(name = name))
                fetchNoteBooks()
                ToastManager.showToast(
                    "Notebook added successfully",
                    isSuccess = true
                )
            } catch (e: Exception) {
                ToastManager.showToast("Error in adding notebook", isSuccess = false)
            }
        }
    }

    private fun deleteNotebookByName(name: String) {
        viewModelScope.launch {
            val notebook = repository.getNotebookByName(name)
            notebook?.let {
                repository.deleteNotebook(it)
                fetchNoteBooks()
                ToastManager.showToast(
                    "Notebook deleted successfully",
                    isSuccess = true
                )
            } ?: run {
                ToastManager.showToast("Notebook not found", isSuccess = false)
            }
        }
    }

    private fun voiceNavigation(name: String) {
        viewModelScope.launch {
            val noteBook = repository.getNotebookByName(name)
            noteBook?.let {
                _uiState.value.globalNavController?.navigate("noteBookDetails/${it.id}")
            } ?: run {
                ToastManager.showToast("Notebook not found", isSuccess = false)
            }
        }
    }

}

data class NoteBookUiState(
    val totalAmount: Int = 0,
    val totalDebt: Int = 0,
    val totalCredit: Int = 0,
    val selectedNoteBookId: Int = -1,
    val noteBookNameString: String = "",
    val listOfNoteBook: List<NoteBookEntity> = emptyList(),
    val isListEmpty: Boolean = false,
    val isVoiceRecognitionStart: Boolean = false,
    val globalNavController: NavHostController? = null,
)


sealed class NoteBookEvent {
    data object FetchNoteBooks : NoteBookEvent()
    data class SetGlobalNavController(val navHostController: NavHostController) : NoteBookEvent()
    data class NavigateToNoteBook(val globalNavController: NavHostController, val noteBookId: Int) :
        NoteBookEvent()

    data class DeleteNoteBooks(val noteBooks: List<Int>) : NoteBookEvent()
    data object StartVoiceRecognition : NoteBookEvent()
    data object StopVoiceRecognition : NoteBookEvent()
    data class AddNoteBookByVoice(val noteBookName: String) : NoteBookEvent()
    data class DeleteNoteBookByVoice(val noteBookName: String) : NoteBookEvent()
    data class OpenNoteBookByVoice(val noteBookName: String) : NoteBookEvent()
    data object OpenNoteBookForm : NoteBookEvent()
}

