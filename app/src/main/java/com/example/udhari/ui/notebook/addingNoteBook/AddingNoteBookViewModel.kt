package com.example.udhari.ui.notebook.addingNoteBook

import androidx.datastore.preferences.protobuf.Empty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhari.data.entity.NoteBookEntity
import com.example.udhari.data.repositories.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddingNoteBookViewModel @Inject constructor(private val repository: FinanceRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow(AddingNoteBookUiState())
    val uiState = _uiState

    fun onEvent(event: AddingNoteBookUiEvent) {
        when (event) {
            is AddingNoteBookUiEvent.InsertNoteBook -> insertNoteBook()

            is AddingNoteBookUiEvent.UpdateNoteBookNameString -> {
                updateNoteBookNameString(event.noteBookNameString)
            }

            AddingNoteBookUiEvent.Success -> {
                _uiState.value = _uiState.value.copy(success = true)
            }

        }
    }

    private fun insertNoteBook() {
        if (_uiState.value.noteBookNameString.isEmpty()) {
            _uiState.value = _uiState.value.copy(isNoteBookEmpty = true)
        } else {
            viewModelScope.launch {
                repository.insertNotebook(NoteBookEntity(name = _uiState.value.noteBookNameString))
            }
            _uiState.value = _uiState.value.copy(noteBookNameString = "")
        }
    }

    private fun updateNoteBookNameString(noteBookNameString: String) {
        _uiState.value = _uiState.value.copy(noteBookNameString = noteBookNameString)
    }

}


data class AddingNoteBookUiState(
    val noteBookNameString: String = "",
    val isLoading: Boolean = false,
    val isNoteBookEmpty: Boolean = false,
    val success: Boolean = false,
)

sealed class AddingNoteBookUiEvent {
    data object InsertNoteBook : AddingNoteBookUiEvent()
    data object Success : AddingNoteBookUiEvent()
    data class UpdateNoteBookNameString(val noteBookNameString: String) : AddingNoteBookUiEvent()
}