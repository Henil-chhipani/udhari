package com.example.udhari.ui.home

import android.util.Log
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
    val selectedNoteBookId: Int = 1,
    val listOfPendingTransaction: List<PendingTransaction> = emptyList(),
    val listOfNoteBook: List<NoteBookEntity> = emptyList(),
    val listOfFinanceEntity: List<FinanceEntity> = emptyList(),
    val noteBookNameString: String = "",
    val noteBook: NoteBookEntity = NoteBookEntity(name = ""),
    val updateNoteBookFlag: Boolean = false,
    val openNoteBookDialog: Boolean = false,
)

sealed class HomeEvent {
    data object FetchNoteBooks : HomeEvent()
    data class FetchFinanceEntity(val id: Int) : HomeEvent()
    data class FetchPendingTransaction(val entityId: Int, val noteBooks: Int) : HomeEvent()
    data object InsertNoteBook : HomeEvent()
    data class AddingNoteBookName(val noteBookName: String) : HomeEvent()
    data object OpenNoteBookDialog : HomeEvent()
    data object CloseNoteBookDialog : HomeEvent()
    data class OnEditNoteBookClick(val noteBook: NoteBookEntity) : HomeEvent()
    data object UpdateSelectedNoteBook : HomeEvent()
    data class DeleteNoteBook(val noteBook: NoteBookEntity) : HomeEvent()
    data class SelectNoteBook(val id: Int) : HomeEvent()
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
            is HomeEvent.FetchNoteBooks -> fetchNoteBooks()

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

            is HomeEvent.InsertNoteBook -> insertNoteBook()
            is HomeEvent.OpenNoteBookDialog -> openNoteBookDialog()
            is HomeEvent.CloseNoteBookDialog -> closeNoteBookDialog()
            is HomeEvent.AddingNoteBookName -> addingNoteBookName(event.noteBookName)
            is HomeEvent.OnEditNoteBookClick -> onEditNoteBookClick(event.noteBook)
            is HomeEvent.UpdateSelectedNoteBook -> updateSelectedNoteBook()
            is HomeEvent.DeleteNoteBook -> deleteNoteBook(event.noteBook)
            is HomeEvent.SelectNoteBook -> selectNoteBook(event.id)
        }
    }

    private fun openNoteBookDialog() {
        _uiState.value = _uiState.value.copy(openNoteBookDialog = true)
    }
    private fun  closeNoteBookDialog() {
        _uiState.value = _uiState.value.copy(noteBookNameString = "")
        _uiState.value = _uiState.value.copy(openNoteBookDialog = false)
    }

    private fun addingNoteBookName(noteBookName: String) {
        _uiState.value = _uiState.value.copy(noteBookNameString = noteBookName)
    }

    private fun onEditNoteBookClick(noteBook: NoteBookEntity) {
        _uiState.value = _uiState.value.copy(noteBookNameString = noteBook.name)
        _uiState.value = _uiState.value.copy(noteBook = noteBook)
        _uiState.value = _uiState.value.copy(updateNoteBookFlag = true)
        Log.d("updateNoteBookDialog", _uiState.value.noteBookNameString)
        openNoteBookDialog()
    }

    private fun updateSelectedNoteBook() {
        viewModelScope.launch {
            var noteBookId = _uiState.value.noteBook.id
            var name = _uiState.value.noteBookNameString
            repository.updateNoteBookEntityNameById(id = noteBookId, name = name)
            _uiState.value = _uiState.value.copy(updateNoteBookFlag = false)
            _uiState.value = _uiState.value.copy(noteBookNameString = "")
            fetchNoteBooks()
            closeNoteBookDialog()
        }
    }

    private fun insertNoteBook() {
        viewModelScope.launch {
            repository.insertNotebook(NoteBookEntity(name = _uiState.value.noteBookNameString))
        }
        _uiState.value = _uiState.value.copy(noteBookNameString = "")
        closeNoteBookDialog()
        fetchNoteBooks()
    }

    private fun fetchNoteBooks() {
        viewModelScope.launch {
            val noteBooks = repository.getAllNotebooks()
            _uiState.value = _uiState.value.copy(listOfNoteBook = noteBooks)
        }
    }

    private fun deleteNoteBook(noteBook: NoteBookEntity) {
        viewModelScope.launch {
            repository.deleteNotebook(noteBook)
        }
        fetchNoteBooks()
    }

    private fun selectNoteBook(id: Int) {
        viewModelScope.launch {
            dataStore.saveNoteBookId(id)
            _uiState.value = _uiState.value.copy(selectedNoteBookId = id)
        }
    }
}