package com.example.udhari.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.udhari.data.dao.FinanceEntityDao
import com.example.udhari.data.dao.NoteBookDao
import com.example.udhari.data.dao.PendingTransactionDao
import com.example.udhari.data.entity.FinanceEntity
import com.example.udhari.data.entity.NoteBookEntity
import com.example.udhari.data.entity.PendingTransaction

@Database(
    entities = [FinanceEntity::class, PendingTransaction::class, NoteBookEntity::class],
    version = 1,
    exportSchema = true
)
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun noteBookDao(): NoteBookDao
    abstract fun pendingTransactionDao(): PendingTransactionDao
    abstract fun financeEntityDao() : FinanceEntityDao
}
