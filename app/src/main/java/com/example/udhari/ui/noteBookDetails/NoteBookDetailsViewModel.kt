package com.example.udhari.ui.noteBookDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhari.data.PreferenceDataStore
import com.example.udhari.data.entity.FinanceEntity
import com.example.udhari.data.entity.NoteBookEntity
import com.example.udhari.data.repositories.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NoteBookDetailsViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val dataStore: PreferenceDataStore
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(NoteBookDetailsUiState())
    val homeUiState: StateFlow<NoteBookDetailsUiState> = _homeUiState.asStateFlow()

    fun onEvent(event: NoteBookDetailsEvent) {
        when (event) {
            is NoteBookDetailsEvent.FetchFinanceEntity -> fetchFinanceEntity()
            is NoteBookDetailsEvent.SelectNoteBook -> selectNoteBook(event.id)
            NoteBookDetailsEvent.FetchTotalAmount -> fetchTotalAmount()
        }
    }

    fun setNoteBookId(id: Int) {
        _homeUiState.value = _homeUiState.value.copy(selectedNoteBookId = id)
    }


    private fun selectNoteBook(id: Int) {
        viewModelScope.launch {
            _homeUiState.value = _homeUiState.value.copy(selectedNoteBookId = id)
            saveNoteBookId(id)
            fetchFinanceEntity()
        }
    }


    private fun saveNoteBookId(id: Int) {
        viewModelScope.launch {
            dataStore.saveNoteBookId(id)
        }
    }


    private fun fetchFinanceEntity() {
        viewModelScope.launch {
            var id = _homeUiState.value.selectedNoteBookId
            var financeEntity = repository.getEntityByNoteBookIdAndTotals(id)
            _homeUiState.value =
                _homeUiState.value.copy(listOfFinanceEntity = financeEntity)
        }
    }

    private fun fetchTotalAmount() {
        viewModelScope.launch {
            var id = _homeUiState.value.selectedNoteBookId
            var financeEntity = repository.getEntityByNoteBookIdAndTotals(id)
            var totalOwe = financeEntity.map { it.totalOwe }.sum()
            var totalCollect = financeEntity.map { it.totalCollect }.sum()
            var totalAmount = totalCollect - totalOwe
            _homeUiState.value =
                _homeUiState.value.copy(
                    totalAmount = totalAmount,
                    totalOwe = totalOwe,
                    totalCollect = totalCollect
                )
        }
    }
}


data class NoteBookDetailsUiState(
    val totalAmount: Double = 0.0,
    val totalOwe: Double = 0.0,
    val totalCollect: Double = 0.0,
    val selectedNoteBookId: Int = 1,
    val listOfFinanceEntity: List<FinanceEntity> = emptyList(),
    val noteBook: NoteBookEntity = NoteBookEntity(name = ""),
    val updateNoteBookFlag: Boolean = false,
    val openNoteBookDialog: Boolean = false,
)

sealed class NoteBookDetailsEvent {
    data object FetchFinanceEntity : NoteBookDetailsEvent()
    data class SelectNoteBook(val id: Int) : NoteBookDetailsEvent()
    data object FetchTotalAmount : NoteBookDetailsEvent()
}