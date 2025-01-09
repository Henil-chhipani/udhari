package com.example.udhari.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "finance_entities",
    foreignKeys = [
        ForeignKey(
            entity = NoteBookEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteBookId"],
            onDelete = ForeignKey.CASCADE // Delete all transactions if the entity is deleted
        )
    ]
)
data class FinanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Unique ID for each entity
    val noteBookId: Int,
    val name: String, // Name of the entity (e.g., Raj, School)
    val phoneNumber: String
)
