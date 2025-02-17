package com.example.udhari.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.udhari.data.entity.NoteBookEntity
import com.example.udhari.data.entity.Totals

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

    @Query("SELECT * FROM note_book WHERE name = :name")
    suspend fun getNotebookByName(name: String): NoteBookEntity

    @Query("SELECT * FROM note_book WHERE id = :id")
    suspend fun getNotebookById(id: Int): NoteBookEntity

    @Delete
    suspend fun deleteNotebook(noteBookEntity: NoteBookEntity)

    @Query("DELETE FROM note_book WHERE id IN (:noteBooks)")
    suspend fun deleteNoteBooks(noteBooks: List<Int>)


    @Query("DELETE FROM note_book WHERE id = :id")
    suspend fun deleteNotebookById(id: Int)

    @Query("""
        SELECT 
            SUM(CASE WHEN type = 'OWE' THEN amount ELSE 0 END) AS totalOwe,
            SUM(CASE WHEN type = 'COLLECT' THEN amount ELSE 0 END) AS totalCollect
        FROM pending_transactions
        WHERE noteBookId = :noteBookId
    """)
    suspend fun getTotalsForNoteBook(noteBookId: Int): Totals
}