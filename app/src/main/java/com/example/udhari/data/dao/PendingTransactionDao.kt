package com.example.udhari.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.udhari.data.entity.PendingTransaction
import com.example.udhari.data.entity.TransactionType


@Dao
interface PendingTransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: PendingTransaction)

    @Update
    suspend fun updateTransaction(transaction: PendingTransaction)

    @Query("SELECT * FROM pending_transactions WHERE type = :type")
    suspend fun getTransactionsByType(type: TransactionType): List<PendingTransaction>

    @Query("SELECT * FROM pending_transactions")
    suspend fun getAllTransactions(): List<PendingTransaction>

    @Query("SELECT * FROM pending_transactions WHERE entityId = :entityId AND noteBookId = :noteBookId")
    suspend fun getTransactionsByEntityIdAndNoteBookId(entityId: Int, noteBookId: Int): List<PendingTransaction>

    @Query("DELETE FROM pending_transactions WHERE id = :transactionId")
    suspend fun deleteTransactionById(transactionId: Int)

    @Query("DELETE FROM pending_transactions WHERE entityId = :entityId")
    suspend fun deleteTransactionsByEntityId(entityId: Int)

    @Query("SELECT SUM(amount) FROM pending_transactions WHERE type = :type AND entityId = :entityId")
    suspend fun getTotalAmountByType(entityId: Int, type: TransactionType): Double

}
