package com.example.udhari.ui.noteBookDetails.addingEntity

import android.util.Log
import androidx.datastore.preferences.protobuf.Empty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhari.data.PreferenceDataStore
import com.example.udhari.data.entity.FinanceEntity
import com.example.udhari.data.repositories.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.lang.Error
import javax.inject.Inject


@HiltViewModel
class AddingEnftityViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val dataStore: PreferenceDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddingEntityUiState())
    val addingEntityUiState: StateFlow<AddingEntityUiState> = _uiState.asStateFlow()


    fun onAddingEntitiyEvent(event: AddingEntityEvent) {

        when (event) {
            is AddingEntityEvent.InsertEntity -> insertEntity()
            is AddingEntityEvent.FetchFinanceEntity -> fetchFinanceEntity()
            is AddingEntityEvent.AddingEntityName -> addingEntityName(event.name)
            is AddingEntityEvent.AddingEntityPhoneNumber -> addingEntityPhoneNumber(event.phoneNumber)
            is AddingEntityEvent.UpdateEntity -> updateEntity(event.financeEntity)
            is AddingEntityEvent.DeleteEntity -> deleteEntity(event.financeEntity)
            is AddingEntityEvent.SetNoteBookId -> setNoteBookId(event.id)
        }
    }

    private fun setNoteBookId(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(noteBookId = id)
        }
    }

    private fun insertEntity() {
        viewModelScope.launch {
            // Check if notebookId is valid
            if (_uiState.value.noteBookId == -1) {
                Log.e("InsertEntity", "NoteBook ID is not set properly")
                return@launch
            }

            // Validate input fields
            when {
                _uiState.value.name.isEmpty() -> {
                    _uiState.value = _uiState.value.copy(isNameEmpty = true)
                    _uiState.value = _uiState.value.copy(nameError = "Name cannot be empty")
                    return@launch
                }

                _uiState.value.phoneNumber.isEmpty() -> {
                    _uiState.value = _uiState.value.copy(isPhoneNumberEmpty = true)
                    _uiState.value =
                        _uiState.value.copy(phoneNumberError = "Phone number cannot be empty")
                    return@launch
                }
            }

            // Insert the entity into the repository
            try {
                repository.insertEntity(
                    FinanceEntity(
                        name = _uiState.value.name,
                        phoneNumber = _uiState.value.phoneNumber,
                        noteBookId = _uiState.value.noteBookId
                    )
                )

                // Fetch updated entities
                fetchFinanceEntity()

                // Reset the form state
                _uiState.value = _uiState.value.copy(
                    name = "",
                    phoneNumber = "",
                    isNameEmpty = false,
                    isPhoneNumberEmpty = false
                )
            } catch (e: Exception) {
                Log.e("InsertEntity", "Failed to insert entity: ${e.message}")
            }
        }
    }

    private fun fetchFinanceEntity() {
        viewModelScope.launch {
            var id = _uiState.value.noteBookId
            _uiState.value = _uiState.value.copy(noteBookId = id)
            var financeEntity = repository.getEntityByNoteBookId(id)
            _uiState.value =
                _uiState.value.copy(listOfFinanceEntity = financeEntity)
        }
    }

    private fun addingEntityName(entityName: String) {
        // Validate name
        val isNameValid =
            entityName.isNotBlank() && entityName.length >= 2 // Example: Minimum 2 characters
        _uiState.value = _uiState.value.copy(
            name = entityName,
            isNameEmpty = !isNameValid,
            nameError = if (!isNameValid) "Name must be at least 2 characters" else ""
        )
    }

    private fun addingEntityPhoneNumber(phoneNumber: String) {
        // Clean the phone number
        val cleanedPhoneNumber = phoneNumber.replace(Regex("[^0-9+]"), "")

        // Validate phone number
        val isPhoneNumberValid = cleanedPhoneNumber.isNotBlank() &&
                cleanedPhoneNumber.length >= 10 && // Example: Minimum 10 digits
                cleanedPhoneNumber.matches(Regex("^[+]?[0-9]{10,15}$")) // Example: Valid international format

        _uiState.value = _uiState.value.copy(
            phoneNumber = cleanedPhoneNumber,
            isPhoneNumberEmpty = !isPhoneNumberValid,
            phoneNumberError = if (!isPhoneNumberValid) "Invalid phone number format" else ""
        )
    }

    private fun updateEntity(financeEntity: FinanceEntity) {
        viewModelScope.launch {
            repository.updateEntity(financeEntity)
            fetchFinanceEntity()
        }
    }

    private fun deleteEntity(financeEntity: FinanceEntity) {
        viewModelScope.launch {
            repository.deleteEntity(financeEntity)
            fetchFinanceEntity()
        }
    }


}

data class AddingEntityUiState(
    val name: String = "",
    val isNameEmpty: Boolean = false,
    val nameError: String = "",
    val phoneNumber: String = "",
    val isPhoneNumberEmpty: Boolean = false,
    val phoneNumberError: String = "",
    val listOfFinanceEntity: List<FinanceEntity> = emptyList(),
    val noteBookId: Int = -1,
    val financeEntity: FinanceEntity = FinanceEntity(
        name = "", noteBookId = -1, phoneNumber = ""
    )

)

sealed class AddingEntityEvent {
    data object InsertEntity : AddingEntityEvent()
    data class SetNoteBookId(val id: Int) : AddingEntityEvent()
    data object FetchFinanceEntity : AddingEntityEvent()
    data class UpdateEntity(val financeEntity: FinanceEntity) : AddingEntityEvent()
    data class DeleteEntity(val financeEntity: FinanceEntity) : AddingEntityEvent()
    data class AddingEntityName(val name: String) : AddingEntityEvent()
    data class AddingEntityPhoneNumber(val phoneNumber: String) : AddingEntityEvent()
}