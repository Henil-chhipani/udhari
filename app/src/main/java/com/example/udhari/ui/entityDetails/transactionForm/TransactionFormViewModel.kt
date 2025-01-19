package com.example.udhari.ui.entityDetails.transactionForm

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhari.data.PreferenceDataStore
import com.example.udhari.data.entity.PendingTransaction
import com.example.udhari.data.entity.TransactionType
import com.example.udhari.data.repositories.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class TransactionFormViewModel @Inject constructor(
    val repository: FinanceRepository,
    val dataStore: PreferenceDataStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionFormUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: TransactionFormEvent) {
        when (event) {
            is TransactionFormEvent.SetEntityId -> _uiState.value =
                _uiState.value.copy(entityId = event.entityId)

            is TransactionFormEvent.AmountChanged -> amountChange(event.amount)
            is TransactionFormEvent.DescriptionChanged -> descriptionChange(event.description)
            is TransactionFormEvent.TypeChange -> typeChange(event.type)
            TransactionFormEvent.GetTodayDate -> getTodayDate()
            TransactionFormEvent.OnCrate -> insertTransaction()
            TransactionFormEvent.FetchNoteBookId -> fetchNoteBookId()
            TransactionFormEvent.FetchTransactions -> fetchTransactions()
        }
    }

    private fun amountChange(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    private fun descriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    private fun typeChange(type: TransactionType) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    @SuppressLint("NewApi")
    fun getTodayDate(): String {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy") // Format as needed
        val date = today.format(formatter)
        _uiState.value = _uiState.value.copy(date = date)
        return date
    }

    fun fetchNoteBookId() {
        viewModelScope.launch {
            dataStore.noteBookId.collect { id ->
                _uiState.value = _uiState.value.copy(noteBookId = id)
            }
        }
    }

    private fun insertTransaction() {
        viewModelScope.launch {
            repository.insertTransaction(
                PendingTransaction(
                    noteBookId = _uiState.value.noteBookId,
                    entityId = _uiState.value.entityId,
                    amount = _uiState.value.amount.toDouble(),
                    description = _uiState.value.description,
                    date = _uiState.value.date,
                    type = _uiState.value.type!!
                )
            )
            fetchTransactions()
            _uiState.value = _uiState.value.copy(
                amount = "",
                description = "",
                date = "",
                type = null
            )
        }
    }

    private fun fetchTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            val transactions = repository.getTransactionsByEntityId(_uiState.value.entityId)
            _uiState.value = _uiState.value.copy(listOfTransaction = transactions)
        }
    }
}

sealed class TransactionFormEvent {
    data class SetEntityId(val entityId: Int) : TransactionFormEvent()
    data object FetchNoteBookId : TransactionFormEvent()
    data class AmountChanged(val amount: String) : TransactionFormEvent()
    data class DescriptionChanged(val description: String) : TransactionFormEvent()
    data class TypeChange(val type: TransactionType) : TransactionFormEvent()
    data object GetTodayDate : TransactionFormEvent()
    data object OnCrate : TransactionFormEvent()
    data object FetchTransactions : TransactionFormEvent()
}

data class TransactionFormUiState(
    val noteBookId: Int = 0,
    val entityId: Int = 0,
    val amount: String = "",
    val description: String = "",
    val date: String = "",
    val type: TransactionType? = null,
    val listOfTransaction: List<PendingTransaction> = emptyList(),
    val isAmountValid: Boolean = true,
    val isDescriptionValid: Boolean = true,
    val isDataValid: Boolean = false,
)