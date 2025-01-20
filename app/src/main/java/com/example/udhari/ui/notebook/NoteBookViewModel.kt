package com.example.udhari.ui.notebook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.udhari.data.PreferenceDataStore
import com.example.udhari.data.entity.NoteBookEntity
import com.example.udhari.data.repositories.FinanceRepository
import com.example.udhari.ui.noteBookDetails.NoteBookDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteBookViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val dataStore: PreferenceDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteBookUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: NoteBookEvent) {
        when (event) {
            is NoteBookEvent.FetchNoteBooks -> fetchNoteBooks()
            is NoteBookEvent.NavigateToNoteBook -> navigateToNoteBook(
                event.noteBookId,
                event.globalNavController)
            is NoteBookEvent.DeleteNoteBook -> TODO()
            NoteBookEvent.UpdateSelectedNoteBook -> TODO()
        }
    }

    private fun navigateToNoteBook(
        noteBookId: Int,
        globalNavController: NavHostController
    ) {
        viewModelScope.launch {
            dataStore.saveNoteBookId(noteBookId)
            globalNavController.navigate("noteBookDetails/${noteBookId}")
        }
    }

    private fun fetchNoteBooks() {
        viewModelScope.launch {
            val noteBooks = repository.getAllNotebooksWithTotals()
            _uiState.value = _uiState.value.copy(listOfNoteBook = noteBooks)
        }
    }

    private fun fetchTotalAmounts(noteBookId: Int) {
        viewModelScope.launch {
            val totalAmounts = repository.getTotalsForNoteBook(noteBookId)
        }
    }

    private fun updateSelectedNoteBook() {
        viewModelScope.launch {

        }
    }

    private fun deleteNoteBook(noteBook: NoteBookEntity) {
        viewModelScope.launch {
            repository.deleteNotebook(noteBook)
        }
        fetchNoteBooks()
    }

}

