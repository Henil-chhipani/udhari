package com.example.udhari.ui.notebook

import com.example.udhari.data.entity.NoteBookEntity

data class NoteBookUiState(
    val totalAmount: Int = 0,
    val totalDebt: Int = 0,
    val totalCredit: Int = 0,
    val selectedNoteBookId: Int = -1,
    val noteBookNameString: String = "",
    val listOfNoteBook: List<NoteBookEntity> = emptyList(),
)
