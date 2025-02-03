package com.example.udhari.ui.notebook

import androidx.navigation.NavHostController
import com.example.udhari.data.entity.NoteBookEntity
import com.example.udhari.ui.noteBookDetails.NoteBookDetailsEvent

sealed class NoteBookEvent {
    data object FetchNoteBooks : NoteBookEvent()
    data class NavigateToNoteBook(val globalNavController: NavHostController, val noteBookId: Int) :
        NoteBookEvent()
    data class DeleteNoteBook(val noteBook: NoteBookEntity) : NoteBookEvent()
    data object UpdateSelectedNoteBook : NoteBookEvent()
}