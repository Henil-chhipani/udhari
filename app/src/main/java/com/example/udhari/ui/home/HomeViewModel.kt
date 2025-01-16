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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val dataStore: PreferenceDataStore
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    fun onHomeScreenEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.FetchNoteBooks -> fetchNoteBooks()

            is HomeEvent.FetchFinanceEntity -> fetchFinanceEntity()
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


    init {
        fetchNoteBookId()
    }

    private fun openNoteBookDialog() {
        _homeUiState.value = _homeUiState.value.copy(openNoteBookDialog = true)
    }

    private fun closeNoteBookDialog() {
        _homeUiState.value = _homeUiState.value.copy(noteBookNameString = "")
        _homeUiState.value = _homeUiState.value.copy(openNoteBookDialog = false)
    }

    private fun addingNoteBookName(noteBookName: String) {
        _homeUiState.value = _homeUiState.value.copy(noteBookNameString = noteBookName)
    }

    private fun onEditNoteBookClick(noteBook: NoteBookEntity) {
        _homeUiState.value = _homeUiState.value.copy(noteBookNameString = noteBook.name)
        _homeUiState.value = _homeUiState.value.copy(noteBook = noteBook)
        _homeUiState.value = _homeUiState.value.copy(updateNoteBookFlag = true)
        openNoteBookDialog()
    }

    private fun updateSelectedNoteBook() {
        viewModelScope.launch {
            var noteBookId = _homeUiState.value.noteBook.id
            var name = _homeUiState.value.noteBookNameString
            repository.updateNoteBookEntityNameById(id = noteBookId, name = name)
            _homeUiState.value = _homeUiState.value.copy(updateNoteBookFlag = false)
            _homeUiState.value = _homeUiState.value.copy(noteBookNameString = "")
            fetchNoteBooks()
            closeNoteBookDialog()
        }
    }

    private fun insertNoteBook() {
        viewModelScope.launch {
            repository.insertNotebook(NoteBookEntity(name = _homeUiState.value.noteBookNameString))
        }
        _homeUiState.value = _homeUiState.value.copy(noteBookNameString = "")
        closeNoteBookDialog()
        fetchNoteBooks()
    }

    private fun fetchNoteBooks() {
        viewModelScope.launch {
            val noteBooks = repository.getAllNotebooks()
            _homeUiState.value = _homeUiState.value.copy(listOfNoteBook = noteBooks)
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

    private fun fetchNoteBookId() {
        viewModelScope.launch {
            dataStore.noteBookId.collect { id ->
                Log.e("NoteBookId in adding viewmodel", "home:" + id.toString())
                _homeUiState.value = _homeUiState.value.copy(selectedNoteBookId = id)
            }
        }
    }


    private fun fetchFinanceEntity() {
        viewModelScope.launch {
            dataStore.noteBookId
                .map { noteBookId ->
                    var id = noteBookId
                    repository.getEntityByNoteBookId(id)
                }
                .collect { financeEntity ->
                    _homeUiState.value =
                        _homeUiState.value.copy(listOfFinanceEntity = financeEntity)
                }
        }
    }


}


// --------------------------------------------------------------------------------------------

data class HomeUiState(
    val totalAmount: Int = 0,
    val totalDebt: Int = 0,
    val totalCredit: Int = 0,
    val selectedNoteBookId: Int = 1,

    val listOfNoteBook: List<NoteBookEntity> = emptyList(),
    val listOfFinanceEntity: List<FinanceEntity> = emptyList(),
    val noteBookNameString: String = "",
    val noteBook: NoteBookEntity = NoteBookEntity(name = ""),
    val updateNoteBookFlag: Boolean = false,
    val openNoteBookDialog: Boolean = false,
)

sealed class HomeEvent {
    data object FetchNoteBooks : HomeEvent()
    data object FetchFinanceEntity : HomeEvent()
    data object InsertNoteBook : HomeEvent()
    data class AddingNoteBookName(val noteBookName: String) : HomeEvent()
    data object OpenNoteBookDialog : HomeEvent()
    data object CloseNoteBookDialog : HomeEvent()
    data class OnEditNoteBookClick(val noteBook: NoteBookEntity) : HomeEvent()
    data object UpdateSelectedNoteBook : HomeEvent()
    data class DeleteNoteBook(val noteBook: NoteBookEntity) : HomeEvent()
    data class SelectNoteBook(val id: Int) : HomeEvent()
}

