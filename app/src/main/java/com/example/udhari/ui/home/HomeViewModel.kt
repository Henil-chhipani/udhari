package com.example.udhari.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhari.data.PreferenceDataStore
import com.example.udhari.data.entity.FinanceEntity
import com.example.udhari.data.entity.NoteBookEntity
import com.example.udhari.data.entity.PendingTransaction
import com.example.udhari.data.repositories.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


data class HomeUiState(
    val totalAmount: Int = 0,
    val totalDebt: Int = 0,
    val totalCredit: Int = 0,
    val listOfPendingTransaction: List<PendingTransaction> = emptyList(),
    val listOfNoteBook: List<NoteBookEntity> = emptyList(),
    val listOfFinanceEntity: List<FinanceEntity> = emptyList()
)

sealed class HomeEvent {
    data object FetchNoteBooks : HomeEvent()
    data class FetchFinanceEntity(val id: Int) : HomeEvent()
    data class FetchPendingTransaction(val entityId: Int, val noteBooks: Int) : HomeEvent()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val dataStore: PreferenceDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.FetchNoteBooks -> {
                viewModelScope.launch {
                    val noteBooks = repository.getAllNotebooks()
                    _uiState.value = _uiState.value.copy(listOfNoteBook = noteBooks)
                }
            }

            is HomeEvent.FetchFinanceEntity -> {
                viewModelScope.launch {
                    val financeEntity = repository.getEntityByNoteBookId(event.id)
                    _uiState.value = _uiState.value.copy(listOfFinanceEntity = financeEntity)
                }
            }

            is HomeEvent.FetchPendingTransaction -> {
                viewModelScope.launch {
                    val pendingTransaction = repository.getTransactionsByEntityIdAndNoteBookId(
                        event.entityId,
                        event.noteBooks
                    )
                    _uiState.value =
                        _uiState.value.copy(listOfPendingTransaction = pendingTransaction)
                }
            }
        }
    }

}