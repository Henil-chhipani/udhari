package com.example.udhari.data.repositories

import com.example.udhari.data.dao.FinanceEntityDao
import com.example.udhari.data.dao.NoteBookDao
import com.example.udhari.data.dao.PendingTransactionDao
import com.example.udhari.data.entity.FinanceEntity
import com.example.udhari.data.entity.NoteBookEntity
import com.example.udhari.data.entity.PendingTransaction
import com.example.udhari.data.entity.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FinanceRepository(
    private val transactionDao: PendingTransactionDao,
    private val entityDao: FinanceEntityDao,
    private val notebookDao: NoteBookDao
) {
    // Transactions
    suspend fun insertTransaction(transaction: PendingTransaction) {
        withContext(Dispatchers.IO) {
            transactionDao.insertTransaction(transaction)
        }
    }

    suspend fun updateTransaction(transaction: PendingTransaction) {
        withContext(Dispatchers.IO) {
            transactionDao.updateTransaction(transaction)
        }
    }

    suspend fun getTransactionsByType(type: TransactionType): List<PendingTransaction> {
        return withContext(Dispatchers.IO) {
            transactionDao.getTransactionsByType(type)
        }
    }

    suspend fun getAllTransactions(): List<PendingTransaction> {
        return withContext(Dispatchers.IO) {
            transactionDao.getAllTransactions()
        }
    }

    suspend fun deleteTransactionById(transactionId: Int) {
        withContext(Dispatchers.IO) {
            transactionDao.deleteTransactionById(transactionId)
        }
    }

    suspend fun getTransactionsByEntityIdAndNoteBookId(
        entityId: Int,
        noteBookId: Int
    ): List<PendingTransaction> {
        return withContext(Dispatchers.IO) {
            transactionDao.getTransactionsByEntityIdAndNoteBookId(entityId, noteBookId)
        }
    }

    suspend fun deleteTransactionsByEntityId(entityId: Int) {
        withContext(Dispatchers.IO) {
            transactionDao.deleteTransactionsByEntityId(entityId)
        }
    }

    suspend fun getTotalAmountByType(entityId: Int, type: TransactionType): Double {
        return withContext(Dispatchers.IO) {
            transactionDao.getTotalAmountByType(entityId, type)
        }
    }

    // Entities
    suspend fun insertEntity(entity: FinanceEntity) {
        withContext(Dispatchers.IO) {
            entityDao.insertEntity(entity)
        }
    }

    suspend fun updateEntity(entity: FinanceEntity) {
        withContext(Dispatchers.IO) {
            entityDao.updateEntity(entity)
        }
    }

    suspend fun getAllEntities(): List<FinanceEntity> {
        return withContext(Dispatchers.IO) {
            entityDao.getAllEntities()
        }
    }

    suspend fun deleteEntity(entity: FinanceEntity) {
        withContext(Dispatchers.IO) {
            entityDao.deleteEntity(entity)
        }
    }

    suspend fun getEntityById(entityId: Int): FinanceEntity? {
        return withContext(Dispatchers.IO) {
            entityDao.getEntityById(entityId)
        }
    }

    suspend fun getEntityByNoteBookId(notebookId: Int): List<FinanceEntity> {
        return withContext(Dispatchers.IO) {
            entityDao.getEntitiesByNoteBookId(notebookId)
        }
    }

    suspend fun searchEntitiesByName(name: String): List<FinanceEntity> {
        return withContext(Dispatchers.IO) {
            entityDao.searchEntitiesByName(name)
        }
    }

    suspend fun insertNotebook(notebook: NoteBookEntity) {
        withContext(Dispatchers.IO) {
            notebookDao.insertNoteBookEntity(notebook)
        }
    }

    suspend fun updateNotebook(notebook: NoteBookEntity) {
        withContext(Dispatchers.IO) {
            notebookDao.updateNoteBookEntity(notebook)
        }
    }

    suspend fun deleteNotebookById(id: Int) {
        withContext(Dispatchers.IO) {
            notebookDao.deleteNotebookById(id)
        }
    }

    suspend fun getAllNotebooks(): List<NoteBookEntity> {
        return withContext(Dispatchers.IO) {
            notebookDao.getAllNotebooks()
        }
    }
}
