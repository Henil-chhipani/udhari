package com.example.udhari.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.udhari.data.entity.FinanceEntity
import com.example.udhari.data.entity.Totals


@Dao
interface FinanceEntityDao {
    @Insert
    suspend fun insertEntity(entity: FinanceEntity)

    @Update
    suspend fun updateEntity(entity: FinanceEntity)

    @Query("SELECT * FROM finance_entities")
    suspend fun getAllEntities(): List<FinanceEntity>

    @Query("SELECT * FROM finance_entities WHERE name = :name")
    suspend fun getEntityByName(name: String): FinanceEntity

    @Delete
    suspend fun deleteEntity(entity: FinanceEntity)

    @Query("SELECT * FROM finance_entities WHERE id = :entityId")
    suspend fun getEntityById(entityId: Int): FinanceEntity?

    @Query("SELECT * FROM finance_entities WHERE noteBookId = :noteBookId")
    suspend fun getEntitiesByNoteBookId(noteBookId: Int): List<FinanceEntity>

    @Query("SELECT * FROM finance_entities WHERE name LIKE '%' || :name || '%'")
    suspend fun searchEntitiesByName(name: String): List<FinanceEntity>

    @Query("DELETE FROM finance_entities WHERE id IN (:entities)")
    suspend fun deleteEntities(entities: List<Int>)

    @Query(
        """
        SELECT 
            SUM(CASE WHEN type = 'OWE' THEN amount ELSE 0 END) AS totalOwe,
            SUM(CASE WHEN type = 'COLLECT' THEN amount ELSE 0 END) AS totalCollect
        FROM pending_transactions
        WHERE entityId = :entityId
    """
    )
    suspend fun getTotalsForEntity(entityId: Int): Totals

}
