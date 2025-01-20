package com.example.udhari.ui.entityDetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhari.data.PreferenceDataStore
import com.example.udhari.data.entity.FinanceEntity
import com.example.udhari.data.entity.PendingTransaction
import com.example.udhari.data.entity.TransactionType
import com.example.udhari.data.repositories.FinanceRepository
import com.example.udhari.ui.entityDetails.transactionForm.TransactionFormEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
            is EntityDetailsUiEvent.AddId -> addId(
                entityId = event.entityId,
                noteBookId = event.noteBookId
            )
            is EntityDetailsUiEvent.DeleteTransaction -> deleteTransaction(event.transaction)
        }
    }

    fun addId(entityId: Int, noteBookId: Int) {
        _uiState.value = _uiState.value.copy(entityId = entityId)
        _uiState.value = _uiState.value.copy(noteBookId = noteBookId)
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
                            noteBookId = -1,
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
                val totalAmount = calculateTotals(transactions)
                _uiState.value = _uiState.value.copy(totalAmount = totalAmount)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(listOfPendingTransaction = emptyList())
            }
        }
    }

    private fun deleteTransaction(transaction: PendingTransaction) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTransactionById(transaction.id)
            fetchPendingTransaction()
        }
    }

    fun calculateTotals(transactions: List<PendingTransaction>): Double {
        val totalToGive = transactions
            .filter { it.type == TransactionType.OWE } // Filter transactions to give
            .sumOf { it.amount } // Sum their amounts

        val totalToCollect = transactions
            .filter { it.type == TransactionType.COLLECT } // Filter transactions to collect
            .sumOf { it.amount } // Sum their amounts

        val totalAmount = totalToCollect - totalToGive

        return totalAmount
    }

}


data class EntityDetailsUiState(
    val noteBookId: Int = -1,
    val entityId: Int = -1,
    val entity: FinanceEntity = FinanceEntity(noteBookId = 0, name = "", phoneNumber = ""),
    val listOfPendingTransaction: List<PendingTransaction> = emptyList(),
    val totalAmount: Double = 0.0,
)

sealed class EntityDetailsUiEvent {
    data class AddId(val entityId: Int, val noteBookId: Int) : EntityDetailsUiEvent()
    data class DeleteTransaction(val transaction: PendingTransaction) : EntityDetailsUiEvent()
}