package com.example.udhari.ui.noteBookDetails

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.udhari.data.PreferenceDataStore
import com.example.udhari.data.entity.FinanceEntity
import com.example.udhari.data.entity.NoteBookEntity
import com.example.udhari.data.repositories.FinanceRepository
import com.example.udhari.navigation.Routes
import com.example.udhari.ui.base.BaseViewModel
import com.example.udhari.ui.entityDetails.EntityDetailsUiEvent
import com.example.udhari.utils.SpeechRecognitionHelper
import com.example.udhari.utils.ToastManager
import com.example.udhari.utils.elseCaseHandling
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NoteBookDetailsViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val speechRecognitionHelper: SpeechRecognitionHelper,
) : BaseViewModel(speechRecognitionHelper) {

    private val _uiState = MutableStateFlow(NoteBookDetailsUiState())
    val homeUiState: StateFlow<NoteBookDetailsUiState> = _uiState.asStateFlow()

    init {
        speechRecognitionHelper.updateHandler { command ->
            handleVoiceCommand(command)
        }
    }


    fun onEvent(event: NoteBookDetailsEvent) {
        when (event) {
            is NoteBookDetailsEvent.FetchFinanceEntity -> fetchFinanceEntity()
            NoteBookDetailsEvent.FetchTotalAmount -> fetchNoteBookTotalAmount()
            NoteBookDetailsEvent.StartVoiceRecognition -> startVoiceRecognition()
            NoteBookDetailsEvent.StopVoiceRecognition -> stopVoiceRecognition()
            is NoteBookDetailsEvent.SetGlobalNavController -> setGlobalNavController(event.navController)
            is NoteBookDetailsEvent.NavigateUpdateNoteBook -> navigateToUpdateNoteBook(event.noteBookId)
            is NoteBookDetailsEvent.DeleteSelectedEntity -> deleteEntities(event.entities)
            is NoteBookDetailsEvent.DeleteEntityByVoice -> deleteEntity(event.name)
        }
    }

    fun setNoteBookId(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val notebook = repository.getNotebookById(id)
                if (notebook != null) {
                    _uiState.value = _uiState.value.copy(
                        selectedNoteBookId = id,
                        noteBook = notebook,
                        isLoading = false
                    )
                    fetchFinanceEntity()
                    fetchNoteBookTotalAmount()
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    Log.e("notebook", "notebook is null")
                }
            } catch (e: Exception) {
                Log.e("notebook", e.toString())
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun setGlobalNavController(navController: NavHostController) {
        _uiState.value = _uiState.value.copy(globalNavController = navController)
    }


    private fun fetchFinanceEntity() {
        viewModelScope.launch {
            var id = _uiState.value.selectedNoteBookId
            try {
                var financeEntity = repository.getEntityByNoteBookIdAndTotals(id)

                _uiState.update { currentState ->
                    currentState.copy(
                        listOfFinanceEntity = financeEntity,
                    )
                }
            } catch (e: Exception) {
                Log.e("fetch", e.toString())
            }

        }
    }

    private fun deleteEntities(entities: List<Int>) {
        viewModelScope.launch {
            try {
                val listOfEntities = entities.toList()
                Log.e("delete", listOfEntities.toString())
                repository.deleteEntities(listOfEntities)
                ToastManager.showToast("Entities Deleted", true)
                fetchFinanceEntity()
            } catch (e: Exception) {
                ToastManager.showToast("Something went wrong", false)
            }
        }
    }

    private fun fetchNoteBookTotalAmount() {
        viewModelScope.launch {
            try {
                var id = _uiState.value.selectedNoteBookId
                var financeEntity = repository.getEntityByNoteBookIdAndTotals(id)
                var totalOwe = financeEntity.map { it.totalOwe }.sum()
                var totalCollect = financeEntity.map { it.totalCollect }.sum()
                var totalAmount = totalCollect - totalOwe

                _uiState.update { it ->
                    it.copy(
                        totalAmount = totalAmount,
                        totalOwe = totalOwe,
                        totalCollect = totalCollect
                    )
                }
            } catch (e: Exception) {
                ToastManager.showToast("Something went wrong", false)
            }
        }
    }

    private fun navigateToUpdateNoteBook(noteBookId: Int) {
        _uiState.value.globalNavController?.navigate(
            Routes.UpdateNoteBook.createRoute(
                noteBookId = _uiState.value.selectedNoteBookId
            )
        )
    }

    override fun handleVoiceCommand(command: String) {
        when {
            command.contains("add entity", ignoreCase = true) -> {
                _uiState.value.globalNavController?.navigate(
                    Routes.AddEntity.createRoute(
                        _uiState.value.selectedNoteBookId
                    )
                )
                onEvent(NoteBookDetailsEvent.StopVoiceRecognition)
            }

            command.contains("create entity", ignoreCase = true) -> {
                _uiState.value.globalNavController?.navigate(
                    Routes.AddEntity.createRoute(
                        _uiState.value.selectedNoteBookId
                    )
                )
                onEvent(NoteBookDetailsEvent.StopVoiceRecognition)
            }

            command.contains("delete entity", ignoreCase = true) -> {
                val name = extractNameFromCommand(command)
                if (name.isNotBlank()) {
                    onEvent(NoteBookDetailsEvent.DeleteEntityByVoice(name))
                } else {
                    ToastManager.showToast("Specify the entity name to delete.", false)
                }
                onEvent(NoteBookDetailsEvent.StopVoiceRecognition)
            }

            command.contains("open entity", ignoreCase = true) -> {
                val details = extractNameFromCommand(command)
                voiceNavigation(details.lowercase())
                onEvent(NoteBookDetailsEvent.StopVoiceRecognition)
            }

            command.contains("back", ignoreCase = true) -> {
                _uiState.value.globalNavController?.popBackStack()
                onEvent(NoteBookDetailsEvent.StopVoiceRecognition)
            }

            else -> {
                elseCaseHandling("Command not recognized") {
                    onEvent(NoteBookDetailsEvent.StopVoiceRecognition)
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

    private fun voiceNavigation(name: String) {
        viewModelScope.launch {
            if (name == "add entity") {
                _uiState.value.globalNavController?.navigate(
                    Routes.AddEntity.createRoute(
                        _uiState.value.selectedNoteBookId
                    )
                )
            } else {
                val entity = repository.getEntityByName(name)
                _uiState.value.globalNavController?.navigate(
                    Routes.EntityDetails.createRoute(
                        _uiState.value.selectedNoteBookId,
                        entity.id
                    )
                )
            }
        }
    }

    private fun deleteEntity(name: String) {
        viewModelScope.launch {
            Log.e("delete", name)
            val entity = repository.getEntityByName(name)
            repository.deleteEntity(entity)
            fetchFinanceEntity()
        }
    }

    private fun extractNameFromCommand(command: String): String {
        return command.substringAfter("entity").trim()
    }
}


data class NoteBookDetailsUiState(
    val totalAmount: Double = 0.0,
    val isLoading: Boolean = false,
    val totalOwe: Double = 0.0,
    val totalCollect: Double = 0.0,
    val selectedNoteBookId: Int = 0,
    val listOfFinanceEntity: List<FinanceEntity> = emptyList(),
    val noteBook: NoteBookEntity = NoteBookEntity(name = ""),
    val updateNoteBookFlag: Boolean = false,
    val openNoteBookDialog: Boolean = false,
    val isVoiceRecognitionStart: Boolean = false,
    val globalNavController: NavHostController? = null
)

sealed class NoteBookDetailsEvent {
    data object FetchFinanceEntity : NoteBookDetailsEvent()
    data class SetGlobalNavController(val navController: NavHostController) : NoteBookDetailsEvent()
    data object FetchTotalAmount : NoteBookDetailsEvent()
    data object StartVoiceRecognition : NoteBookDetailsEvent()
    data object StopVoiceRecognition : NoteBookDetailsEvent()
    data class NavigateUpdateNoteBook(val noteBookId: Int) : NoteBookDetailsEvent()
    data class DeleteSelectedEntity(val entities: List<Int>) : NoteBookDetailsEvent()
    data class DeleteEntityByVoice(val name: String) : NoteBookDetailsEvent()
}