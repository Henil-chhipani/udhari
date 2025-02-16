package com.example.udhari.ui.notebook

import androidx.navigation.NavHostController
import com.example.udhari.data.entity.NoteBookEntity
import com.example.udhari.ui.noteBookDetails.NoteBookDetailsEvent

sealed class NoteBookEvent {
    data object FetchNoteBooks : NoteBookEvent()
    data class SetGlobalNavController(val navHostController: NavHostController) : NoteBookEvent()
    data class NavigateToNoteBook(val globalNavController: NavHostController, val noteBookId: Int) :
        NoteBookEvent()
    data class DeleteNoteBooks(val noteBooks: List<Int>): NoteBookEvent()
    data object StartVoiceRecognition: NoteBookEvent()
    data object StopVoiceRecognition: NoteBookEvent()
    data class AddNoteBookByVoice(val noteBookName: String): NoteBookEvent()
    data class DeleteNoteBookByVoice(val noteBookName: String):  NoteBookEvent()
    data class OpenNoteBookByVoice(val noteBookName: String): NoteBookEvent()
    data object OpenNoteBookForm: NoteBookEvent()
}