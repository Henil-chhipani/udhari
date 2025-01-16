package com.example.udhari.ui.entityDetails.transactionForm

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.example.udhari.data.entity.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class TransactionFormViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionFormUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: TransactionFormEvent) {
        when (event) {
            is TransactionFormEvent.AmountChanged -> amountChange(event.amount)
            is TransactionFormEvent.DescriptionChanged -> descriptionChange(event.description)
            is TransactionFormEvent.TypeChange -> typeChange(event.type)
            TransactionFormEvent.getTodayDate -> getTodayDate()
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
}

sealed class TransactionFormEvent {
    data class AmountChanged(val amount: String) : TransactionFormEvent()
    data class DescriptionChanged(val description: String) : TransactionFormEvent()
    data class TypeChange(val type: TransactionType) : TransactionFormEvent()
    data object getTodayDate : TransactionFormEvent()
}

data class TransactionFormUiState(
    val amount: String = "",
    val description: String = "",
    val date: String = "",
    val type: TransactionType? = null,
    val isAmountValid: Boolean = true,
    val isDescriptionValid: Boolean = true,
    val isDataValid: Boolean = false,
)