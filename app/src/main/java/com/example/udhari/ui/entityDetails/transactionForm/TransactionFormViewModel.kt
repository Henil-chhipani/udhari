package com.example.udhari.ui.entityDetails.transactionForm

import androidx.lifecycle.ViewModel
import com.example.udhari.ui.entityDetails.EntityDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class TransactionFormViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionFormUiState())
    val uiState = _uiState.asStateFlow()



}

sealed class TransactionFormEvent {
    data class AmountChanged(val amount: String) : TransactionFormEvent()
    data class DescriptionChanged(val description: String) : TransactionFormEvent()
}

data class TransactionFormUiState(
    val amount: String = "",
    val description: String = "",
    val isAmountValid: Boolean = true,
    val isDescriptionValid: Boolean = true,
    val isDataValid: Boolean = false,
)