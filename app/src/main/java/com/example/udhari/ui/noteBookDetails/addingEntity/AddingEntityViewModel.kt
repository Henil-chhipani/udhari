package com.example.udhari.ui.noteBookDetails.addingEntity

import android.util.Log
import androidx.datastore.preferences.protobuf.Empty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.udhari.data.PreferenceDataStore
import com.example.udhari.data.entity.FinanceEntity
import com.example.udhari.data.repositories.FinanceRepository
import com.example.udhari.ui.base.BaseViewModel
import com.example.udhari.ui.entityDetails.EntityDetailsUiEvent
import com.example.udhari.ui.notebook.addingNoteBook.AddingNoteBookUiEvent
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
import java.lang.Error
import javax.inject.Inject


@HiltViewModel
class AddingEnftityViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val speechRecognitionHelper: SpeechRecognitionHelper,
) : BaseViewModel(speechRecognitionHelper) {

    private val _uiState = MutableStateFlow(AddingEntityUiState())
    val addingEntityUiState: StateFlow<AddingEntityUiState> = _uiState.asStateFlow()

    init {
        speechRecognitionHelper.updateHandler { command ->
            handleVoiceCommand(command)
        }
    }

    fun onAddingEntitiyEvent(event: AddingEntityEvent) {

        when (event) {
            is AddingEntityEvent.InsertEntity -> insertEntity()
            is AddingEntityEvent.AddingEntityName -> addingEntityName(event.name)
            is AddingEntityEvent.AddingEntityPhoneNumber -> addingEntityPhoneNumber(event.phoneNumber)
            is AddingEntityEvent.UpdateEntity -> updateEntity()
            is AddingEntityEvent.SetNoteBookId -> setNoteBookId(event.id)
            AddingEntityEvent.StartVoiceRecognition -> startVoiceRecognition()
            AddingEntityEvent.StopVoiceRecognition -> stopVoiceRecognition()
            is AddingEntityEvent.SetEntityIdAndNotebookId -> setEntityIdAndNotebookId(
                event.entityId,
                event.noteBookId
            )

            is AddingEntityEvent.SetGlobalNavController -> setGlobalNavController(event.navController)
        }
    }

    private fun setNoteBookId(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(noteBookId = id)
        }
    }

    private fun setGlobalNavController(navController: NavHostController) {
        _uiState.value = _uiState.value.copy(globalNavController = navController)
    }

    private fun setEntityIdAndNotebookId(entityId: Int, noteBookId: Int) {
        viewModelScope.launch {
            val entity = repository.getEntityById(entityId)

            if (entity != null) {
                _uiState.value = _uiState.value.copy(
                    noteBookId = noteBookId,
                    entity = entity,
                    name = entity.name,
                    phoneNumber = entity.phoneNumber,
                    isUpdate = true
                )
            }
        }
    }

    private fun updateEntity() {
        viewModelScope.launch {
            try {
                repository.updateEntity(
                    FinanceEntity(
                        id = _uiState.value.entity!!.id,
                        name = _uiState.value.name.lowercase(),
                        phoneNumber = _uiState.value.phoneNumber,
                        noteBookId = _uiState.value.noteBookId
                    )
                )
                _uiState.value.globalNavController?.popBackStack()
                ToastManager.showToast("Updated Successfully", isSuccess = true)
            } catch (e: Exception) {
                ToastManager.showToast("Failed to update entity", isSuccess = false)
            }
        }
    }

    private fun insertEntity() {
        viewModelScope.launch {
            // Check if notebookId is valid
            if (_uiState.value.noteBookId == -1) {
                ToastManager.showToast("Failed to create entity", isSuccess = false)
                Log.e("InsertEntity", "NoteBook ID is not set properly")
                return@launch
            }

            // Validate input fields
            when {
                _uiState.value.name.isEmpty() -> {
                    _uiState.update {
                        it.copy(
                            isNameEmpty = true,
                            nameError = "Name cannot be empty"
                        )
                    }
                    return@launch
                }

                _uiState.value.phoneNumber.isEmpty() -> {
                    _uiState.update {
                        it.copy(
                            isPhoneNumberEmpty = true,
                            phoneNumberError = "Phone number cannot be empty"
                        )
                    }
                    return@launch
                }

                _uiState.value.phoneNumberError.isEmpty() && _uiState.value.nameError.isEmpty() -> {
                    try {
                        repository.insertEntity(
                            FinanceEntity(
                                name = _uiState.value.name.lowercase(),
                                phoneNumber = _uiState.value.phoneNumber,
                                noteBookId = _uiState.value.noteBookId
                            )
                        )

                        // Reset the form state
                        _uiState.update {
                            it.copy(
                                name = "",
                                phoneNumber = "",
                                isNameEmpty = false,
                                isPhoneNumberEmpty = false
                            )
                        }
                        ToastManager.showToast("Entity added successfully", isSuccess = true)
                    } catch (e: Exception) {
                        ToastManager.showToast("Failed to create entity", isSuccess = false)
                    }
                }
            }
        }
    }

    private fun addingEntityName(entityName: String) {
        // Validate name
        val isNameValid =
            entityName.isNotBlank() && entityName.isNotEmpty() // Example: Minimum 2 characters

        _uiState.update { currentState ->
            currentState.copy(
                name = entityName.lowercase(),
                isNameEmpty = !isNameValid,
                nameError = if (!isNameValid) "Name must be at least 2 characters" else ""
            )
        }
    }

    private fun addingEntityPhoneNumber(phoneNumber: String) {
        // Clean the phone number
        val cleanedPhoneNumber = phoneNumber.replace(Regex("[^0-9+]"), "")

        // Validate phone number
        val isPhoneNumberValid = cleanedPhoneNumber.isNotBlank() &&
                cleanedPhoneNumber.length >= 10 &&  // Example: Minimum 10 digits
                cleanedPhoneNumber.matches(Regex("^[+]?[0-9]{10}$")) // Example: Valid international format

        _uiState.value = _uiState.value.copy(
            phoneNumber = cleanedPhoneNumber,
            isPhoneNumberEmpty = !isPhoneNumberValid,
            phoneNumberError = if (!isPhoneNumberValid) "Phone number must be exactly 10 digits" else ""
        )
    }


    override fun startVoiceRecognition() {
        super.startVoiceRecognition()
        _uiState.value = _uiState.value.copy(isVoiceRecognitionStart = true)
    }

    override fun stopVoiceRecognition() {
        super.stopVoiceRecognition()
        _uiState.value = _uiState.value.copy(isVoiceRecognitionStart = false)
    }

    override fun handleVoiceCommand(command: String) {
        when {
            command.contains("name", ignoreCase = true) -> {
                val name = extractNameFromCommand(command)
                onAddingEntitiyEvent(AddingEntityEvent.AddingEntityName(name))
                onAddingEntitiyEvent(AddingEntityEvent.StopVoiceRecognition)
                stopVoiceRecognition()
            }

            command.contains("phone", ignoreCase = true) -> {
                val phoneNumber = command.replace(Regex("[^0-9+]"), "")
                onAddingEntitiyEvent(AddingEntityEvent.AddingEntityPhoneNumber(phoneNumber))
                onAddingEntitiyEvent(AddingEntityEvent.StopVoiceRecognition)
                stopVoiceRecognition()
            }

            command.contains("number", ignoreCase = true) -> {
                val phoneNumber = command.replace(Regex("[^0-9+]"), "")
                onAddingEntitiyEvent(AddingEntityEvent.AddingEntityPhoneNumber(phoneNumber))
                onAddingEntitiyEvent(AddingEntityEvent.StopVoiceRecognition)
                stopVoiceRecognition()
            }

            command.contains("insert", ignoreCase = true) -> {
                onAddingEntitiyEvent(AddingEntityEvent.InsertEntity)
                onAddingEntitiyEvent(AddingEntityEvent.StopVoiceRecognition)
                stopVoiceRecognition()
            }

            command.contains("back", ignoreCase = true) -> {
                _uiState.value.globalNavController?.popBackStack()
                stopVoiceRecognition()
            }

            else -> {
                elseCaseHandling("Command not recognized") {
                    onAddingEntitiyEvent(AddingEntityEvent.StopVoiceRecognition)
                }
            }
        }
    }

    fun extractNameFromCommand(command: String): String {
        val nameRegex = Regex("name\\s+(.+)", RegexOption.IGNORE_CASE)
        val matchResult = nameRegex.find(command)
        return matchResult?.groupValues?.get(1) ?: ""
    }

}

data class AddingEntityUiState(
    val name: String = "",
    val isNameEmpty: Boolean = false,
    val nameError: String = "",
    val phoneNumber: String = "",
    val isPhoneNumberEmpty: Boolean = false,
    val phoneNumberError: String = "",
    val noteBookId: Int = -1,
    val financeEntity: FinanceEntity = FinanceEntity(
        name = "", noteBookId = -1, phoneNumber = ""
    ),
    val isVoiceRecognitionStart: Boolean = false,
    val globalNavController: NavHostController? = null,
    val isUpdate: Boolean = false,
    val entity: FinanceEntity? = null
)

sealed class AddingEntityEvent {
    data object InsertEntity : AddingEntityEvent()
    data class SetGlobalNavController(val navController: NavHostController) : AddingEntityEvent()
    data class SetNoteBookId(val id: Int) : AddingEntityEvent()
    data object UpdateEntity : AddingEntityEvent()
    data class AddingEntityName(val name: String) : AddingEntityEvent()
    data class AddingEntityPhoneNumber(val phoneNumber: String) : AddingEntityEvent()
    data object StartVoiceRecognition : AddingEntityEvent()
    data object StopVoiceRecognition : AddingEntityEvent()
    data class SetEntityIdAndNotebookId(val entityId: Int, val noteBookId: Int) :
        AddingEntityEvent()
}