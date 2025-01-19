package com.example.udhari.ui.addingEntity

import android.util.Log
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
import javax.inject.Inject


@HiltViewModel
class AddingEnftityViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val dataStore: PreferenceDataStore
) : ViewModel() {

    private val _addingEntityUiState = MutableStateFlow(AddingEntityUiState())
    val addingEntityUiState: StateFlow<AddingEntityUiState> = _addingEntityUiState.asStateFlow()


    fun onAddingEntitiyEvent(event: AddingEntityEvent) {

        when (event) {
            is AddingEntityEvent.InsertEntity -> insertEntity()
            is AddingEntityEvent.FetchFinanceEntity -> fetchFinanceEntity()
            is AddingEntityEvent.AddingEntityName -> addingEntityName(event.name)
            is AddingEntityEvent.AddingEntityPhoneNumber -> addingEntityPhoneNumber(event.phoneNumber)
            is AddingEntityEvent.UpdateEntity -> updateEntity(event.financeEntity)
            is AddingEntityEvent.DeleteEntity -> deleteEntity(event.financeEntity)
            is AddingEntityEvent.FetchNoteBookId -> fetchNoteBookId()
        }
    }

    private fun insertEntity() {
        viewModelScope.launch {
            repository.insertEntity(
                FinanceEntity(
                    name = _addingEntityUiState.value.name,
                    phoneNumber = _addingEntityUiState.value.phoneNumber,
                    noteBookId = _addingEntityUiState.value.noteBookId
                )
            )
            fetchFinanceEntity()
            _addingEntityUiState.value = _addingEntityUiState.value.copy(
                name = "",
                phoneNumber = ""
            )
        }
    }

    fun fetchNoteBookId() {
        viewModelScope.launch {
            dataStore.noteBookId.collect { id ->
                _addingEntityUiState.value = _addingEntityUiState.value.copy(noteBookId = id)
            }
        }
    }


    private fun fetchFinanceEntity() {
        viewModelScope.launch {
            dataStore.noteBookId
                .map { noteBookId ->
                    var id = noteBookId
                    _addingEntityUiState.value = _addingEntityUiState.value.copy(noteBookId = id)
                    repository.getEntityByNoteBookId(id)
                }
                .collect { financeEntity ->
                    _addingEntityUiState.value =
                        _addingEntityUiState.value.copy(listOfFinanceEntity = financeEntity)
                }
        }
    }

    private fun addingEntityName(entityName: String) {
        _addingEntityUiState.value = _addingEntityUiState.value.copy(name = entityName)
    }

    private fun addingEntityPhoneNumber(phoneNumber: String) {
        val cleanedPhoneNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
        _addingEntityUiState.value = _addingEntityUiState.value.copy(
            phoneNumber = cleanedPhoneNumber,
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
    val phoneNumber: String = "",
    val listOfFinanceEntity: List<FinanceEntity> = emptyList(),
    val noteBookId: Int = 1,
    val financeEntity: FinanceEntity = FinanceEntity(
        name="", noteBookId = 1, phoneNumber = ""
    )
)

sealed class AddingEntityEvent {
    data object InsertEntity : AddingEntityEvent()
    data object FetchFinanceEntity : AddingEntityEvent()
    data class UpdateEntity(val financeEntity: FinanceEntity) : AddingEntityEvent()
    data class DeleteEntity(val financeEntity: FinanceEntity) : AddingEntityEvent()
    data class AddingEntityName(val name: String) : AddingEntityEvent()
    data class AddingEntityPhoneNumber(val phoneNumber: String) : AddingEntityEvent()
    data object FetchNoteBookId : AddingEntityEvent()
}