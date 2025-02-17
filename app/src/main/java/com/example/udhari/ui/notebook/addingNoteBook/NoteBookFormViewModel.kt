package com.example.udhari.ui.notebook.addingNoteBook

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.udhari.data.entity.NoteBookEntity
import com.example.udhari.data.repositories.FinanceRepository
import com.example.udhari.ui.base.BaseViewModel
import com.example.udhari.ui.noteBookDetails.addingEntity.AddingEntityEvent
import com.example.udhari.utils.SpeechRecognitionHelper
import com.example.udhari.utils.ToastManager
import com.example.udhari.utils.elseCaseHandling
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


    @HiltViewModel
    class NoteBookFormViewModel @Inject constructor(
        private val repository: FinanceRepository,
        speechRecognitionHelper: SpeechRecognitionHelper,
    ) : BaseViewModel(speechRecognitionHelper) {

        private val _uiState = MutableStateFlow(AddingNoteBookUiState())
        val uiState = _uiState.asStateFlow()


        fun onEvent(event: AddingNoteBookUiEvent) {
            when (event) {
                is AddingNoteBookUiEvent.InsertNoteBook -> insertNoteBook()
                is AddingNoteBookUiEvent.UpdateNoteBookNameString -> {
                    updateNoteBookNameString(event.noteBookNameString)
                }

                is AddingNoteBookUiEvent.UpdateNoteBook -> updateNotebook()
                is AddingNoteBookUiEvent.IsNoteBookUpdate -> isNoteBookUpdate(
                    event.isUpdate,
                    event.noteBookId
                )

                is AddingNoteBookUiEvent.SetGlobalNavController -> setGlobalNavController(event.navHostController)
                AddingNoteBookUiEvent.StartVoiceRecognition -> startVoiceRecognition()
                AddingNoteBookUiEvent.StopVoiceRecognition -> stopVoiceRecognition()
            }
        }

        private fun setGlobalNavController(navHostController: NavHostController) {
            _uiState.value = _uiState.value.copy(globalNavController = navHostController)
        }

        private fun insertNoteBook() {
            if (_uiState.value.noteBookNameString.isEmpty()) {
                _uiState.update { it->
                    it.copy(isNoteBookError = true, noteBookErrorMessage = "NoteBook Name can't be empty")
                }
            } else {
                viewModelScope.launch {
                    try {
                        repository.insertNotebook(NoteBookEntity(name = _uiState.value.noteBookNameString.lowercase()))
                        ToastManager.showToast("Notebook added successfully", isSuccess = true)
                        _uiState.update { it.copy(noteBookNameString = "") }
                    } catch (e: Exception) {
                        ToastManager.showToast("Failed to add notebook", isSuccess = false)
                    }
                }
            }
        }

        private fun updateNoteBookNameString(noteBookNameString: String) {
            // Trim leading and trailing spaces
            val trimmedName = noteBookNameString.trim()

            // Check if the notebook name is empty
            if (trimmedName.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    noteBookNameString = "",
                    isNoteBookError = true,
                    noteBookErrorMessage = "Notebook name cannot be empty"
                )
                return
            }

            // Validate length (Example: Ensure it's at least 3 characters)
            if (trimmedName.length < 3) {
                _uiState.value = _uiState.value.copy(
                    noteBookNameString = noteBookNameString,
                    isNoteBookError = true,
                    noteBookErrorMessage = "Notebook name must be at least 3 characters long"
                )
                return
            }

            // Ensure name is not too long (Example: Limit to 50 characters)
            if (trimmedName.length > 50) {
                _uiState.value = _uiState.value.copy(
                    noteBookNameString = noteBookNameString,
                    isNoteBookError = true,
                    noteBookErrorMessage = "Notebook name cannot exceed 50 characters"
                )
                return
            }

            // Validate against special characters (Allow only letters, numbers, and spaces)
            val isValidName = trimmedName.matches(Regex("^[a-zA-Z0-9 ]+$"))
            if (!isValidName) {
                _uiState.value = _uiState.value.copy(
                    noteBookNameString = noteBookNameString,
                    isNoteBookError = true,
                    noteBookErrorMessage = "Notebook name can only contain letters, numbers, and spaces"
                )
                return
            }

            // Update state with valid name
            _uiState.value = _uiState.value.copy(
                noteBookNameString = noteBookNameString.lowercase(),
                isNoteBookError = false,
                noteBookErrorMessage = ""
            )
        }


        override fun handleVoiceCommand(command: String) {

            when {
                command.contains("name") -> {
                    val name = extractNameFromCommand(command)
                    onEvent(AddingNoteBookUiEvent.UpdateNoteBookNameString(name))
                    onEvent(AddingNoteBookUiEvent.StopVoiceRecognition)
                }

                command.contains("insert") -> {
                    onEvent(AddingNoteBookUiEvent.InsertNoteBook)
                    onEvent(AddingNoteBookUiEvent.StopVoiceRecognition)
                }

                command.contains("back", ignoreCase = true) -> {
                    _uiState.value.globalNavController?.popBackStack()
                    onEvent(AddingNoteBookUiEvent.StopVoiceRecognition)
                }

                else -> {
                    elseCaseHandling("Command not recognized") {
                        onEvent(AddingNoteBookUiEvent.StopVoiceRecognition)
                    }
                }
            }
        }

        private fun extractNameFromCommand(command: String): String {
            val nameRegex = Regex("name\\s+(.+)", RegexOption.IGNORE_CASE)
            val matchResult = nameRegex.find(command)
            return matchResult?.groupValues?.get(1) ?: ""
        }

        override fun startVoiceRecognition() {
            super.startVoiceRecognition()
            _uiState.value = _uiState.value.copy(isVoiceRecognitionStart = true)
        }

        override fun stopVoiceRecognition() {
            super.stopVoiceRecognition()
            _uiState.value = _uiState.value.copy(isVoiceRecognitionStart = false)
        }


        private fun updateNotebook() {
            viewModelScope.launch {
                try {
                    repository.updateNotebook(
                        NoteBookEntity(
                            id = _uiState.value.noteBook!!.id,
                            name = _uiState.value.noteBookNameString
                        )
                    )
                    ToastManager.showToast("Notebook updated successfully", isSuccess = true)
                    _uiState.value.globalNavController!!.popBackStack()
                } catch (e: Exception) {
                    ToastManager.showToast("Failed to update notebook", isSuccess = false)
                }

            }
        }

        private fun isNoteBookUpdate(isNoteBookUpdate: Boolean, noteBookId: Int) {
            viewModelScope.launch {
                var notebook = repository.getNotebookById(noteBookId)
                if (notebook != null) {
                    _uiState.update { it ->
                        it.copy(
                            isUpdate = isNoteBookUpdate,
                            noteBook = notebook,
                            noteBookNameString = notebook.name
                        )
                    }
                } else {
                    _uiState.value.globalNavController!!.popBackStack()
                }
            }
        }
    }


data class AddingNoteBookUiState(
    val isNoteBookError: Boolean = false,
    val noteBookNameString: String = "",
    val noteBookErrorMessage: String = "",
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val isUpdate: Boolean = false,
    val noteBook: NoteBookEntity? = null,
    val globalNavController: NavHostController? = null,
    val isVoiceRecognitionStart: Boolean = false
)

sealed class AddingNoteBookUiEvent {
    data object InsertNoteBook : AddingNoteBookUiEvent()
    data class SetGlobalNavController(val navHostController: NavHostController) :
        AddingNoteBookUiEvent()

    data class UpdateNoteBookNameString(val noteBookNameString: String) : AddingNoteBookUiEvent()
    data object UpdateNoteBook : AddingNoteBookUiEvent()
    data class IsNoteBookUpdate(val isUpdate: Boolean, val noteBookId: Int) :
        AddingNoteBookUiEvent()

    data object StartVoiceRecognition : AddingNoteBookUiEvent()
    data object StopVoiceRecognition : AddingNoteBookUiEvent()
}