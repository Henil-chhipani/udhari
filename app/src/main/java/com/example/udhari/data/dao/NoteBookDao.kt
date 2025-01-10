package com.example.udhari.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.udhari.data.entity.NoteBookEntity

@Dao
interface NoteBookDao {
    @Insert
    fun insertNoteBookEntity(noteBookEntity: NoteBookEntity)

    @Update
    fun updateNoteBookEntity(noteBookEntity: NoteBookEntity)

    @Query("UPDATE  note_book SET name = :name WHERE id = :id")
    fun updateNoteBookEntityNameById(name: String,id: Int)

    @Query("SELECT * FROM note_book")
    suspend fun getAllNotebooks(): List<NoteBookEntity>

    @Delete
    suspend fun deleteNotebook(noteBookEntity: NoteBookEntity)

    @Query("DELETE FROM note_book WHERE id = :id")
    suspend fun deleteNotebookById(id: Int)
}