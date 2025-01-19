package com.example.udhari.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "note_book")
data class NoteBookEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Unique ID for each entity
    val name: String,// Name of the entity (e.g., Bussiness note, personal note)
    val totalOwe: Double = 0.0, // Total amount to give (optional)
    val totalCollect: Double = 0.0
)
