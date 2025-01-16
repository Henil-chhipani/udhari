package com.example.udhari.ui.entityDetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhari.data.PreferenceDataStore
import com.example.udhari.data.entity.FinanceEntity
import com.example.udhari.data.entity.PendingTransaction
import com.example.udhari.data.repositories.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EntityDetailsViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val dataStore: PreferenceDataStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(EntityDetailsUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: EntityDetailsUiEvent) {
        when (event) {
            is EntityDetailsUiEvent.AddEntityId -> addEntityId(event.id)
        }
    }

    fun addEntityId(entityId: Int) {
        _uiState.value = _uiState.value.copy(entityId = entityId)
        fetchEntity()
        fetchPendingTransaction()
    }

    private fun fetchEntity() {
        viewModelScope.launch {
            val entityId = _uiState.value.entityId
            if (entityId == null) {
                // Handle the case where entityId is not set
                _uiState.value = _uiState.value.copy(
                    entity = FinanceEntity(
                        noteBookId = 0,
                        name = "not available",
                        phoneNumber = "not available"
                    )
                )
                return@launch
            }

            try {
                val entity = repository.getEntityById(entityId)
                if (entity != null) {
                    _uiState.value = _uiState.value.copy(entity = entity)
                } else {
                    // Handle the case where the entity is not found
                    _uiState.value = _uiState.value.copy(
                        entity = FinanceEntity(
                            noteBookId = 0,
                            name = "not available",
                            phoneNumber = "not available"
                        )
                    )
                    // Optionally, show an error message to the user
                }
            } catch (e: Exception) {
                // Handle exceptions (e.g., network errors, database errors)
                Log.e("EntityViewModel", "Error fetching entity", e)
                _uiState.value = _uiState.value.copy(
                    entity = FinanceEntity(
                        noteBookId = 0,
                        name = "not available",
                        phoneNumber = "not available"
                    )
                )
                // Optionally, show an error message to the user
            }
        }
    }

    private fun fetchPendingTransaction() {
        viewModelScope.launch {
            val entityId = _uiState.value.entityId
            if (entityId == null) {
                // Handle the case where entityId is not set
                _uiState.value = _uiState.value.copy(listOfPendingTransaction = emptyList())
                return@launch
            }

            try {
                val transactions = repository.getTransactionsByEntityId(entityId)
                _uiState.value = _uiState.value.copy(listOfPendingTransaction = transactions)
            } catch (e: Exception) {
                // Handle exceptions (e.g., network errors, database errors)
                Log.e("EntityViewModel", "Error fetching transactions", e)
                _uiState.value = _uiState.value.copy(listOfPendingTransaction = emptyList())
                // Optionally, show an error message to the user
            }
        }
    }
}

data class EntityDetailsUiState(
    val entityId: Int = 0,
    val entity: FinanceEntity = FinanceEntity(noteBookId = 0, name = "", phoneNumber = ""),
    val listOfPendingTransaction: List<PendingTransaction> = emptyList(),
)

sealed class EntityDetailsUiEvent {
    data class AddEntityId(val id: Int) : EntityDetailsUiEvent()
}