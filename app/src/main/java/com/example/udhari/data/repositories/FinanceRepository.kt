package com.example.udhari.data.repositories

import android.util.Log
import androidx.compose.ui.platform.LocalGraphicsContext
import com.example.udhari.data.dao.FinanceEntityDao
import com.example.udhari.data.dao.NoteBookDao
import com.example.udhari.data.dao.PendingTransactionDao
import com.example.udhari.data.entity.FinanceEntity
import com.example.udhari.data.entity.NoteBookEntity
import com.example.udhari.data.entity.PendingTransaction
import com.example.udhari.data.entity.Totals
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
            Log.d("FinanceRepository", "Inserting transaction: $transaction")
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



    suspend fun getTransactionsByEntityId(
        entityId: Int,
    ): List<PendingTransaction> {
        return withContext(Dispatchers.IO) {
            transactionDao.getTransactionsByEntityId(entityId)
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

    // get overall total of data base regardless of notebook and entity
    suspend fun getOverallTotals(): Totals {
        return withContext(Dispatchers.IO) {
            transactionDao.getOverallTotals()
        }
    }

    // -------------------------------------------------------------------------------------------
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

    suspend fun getEntityByName(name: String): FinanceEntity {
        return withContext(Dispatchers.IO) {
            entityDao.getEntityByName(name)
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

    suspend fun deleteEntities(entities: List<Int>) {
        withContext(Dispatchers.IO) {
            try {
                entityDao.deleteEntities(entities)
            } catch (e: Exception) {
                Log.e("DeleteNoteBooks", "Failed to delete notebooks: ${e.message}")
                // Optionally, handle the exception further
            }
        }
    }

    suspend fun getEntityByNoteBookIdAndTotals(notebookId: Int): List<FinanceEntity> =
        withContext(Dispatchers.IO) {
            val entities = entityDao.getEntitiesByNoteBookId(notebookId)
            return@withContext entities.map {
                val totals = entityDao.getTotalsForEntity(it.id)
                it.copy(
                    totalOwe = totals.totalOwe,
                    totalCollect = totals.totalCollect
                )
            }
        }


    suspend fun searchEntitiesByName(name: String): List<FinanceEntity> {
        return withContext(Dispatchers.IO) {
            entityDao.searchEntitiesByName(name)
        }
    }

    // provide total for particular entity
    suspend fun getTotalsForEntity(entityId: Int): Totals {
        return withContext(Dispatchers.IO) {
            entityDao.getTotalsForEntity(entityId)
        }
    }

    // -------------------------------------------------------------------------------------------
    suspend fun insertNotebook(notebook: NoteBookEntity) {
        withContext(Dispatchers.IO) {
            notebookDao.insertNoteBookEntity(notebook)
        }
    }


    suspend fun getNotebookByName(name: String): NoteBookEntity? {
        return withContext(Dispatchers.IO) {
            notebookDao.getNotebookByName(name)
        }
    }

    suspend fun getNotebookById(id: Int): NoteBookEntity? {
        return withContext(Dispatchers.IO) {
            notebookDao.getNotebookById(id)
        }
    }

    suspend fun updateNotebook(notebook: NoteBookEntity) {
        withContext(Dispatchers.IO) {
            notebookDao.updateNoteBookEntity(notebook)
        }
    }

    suspend fun updateNoteBookEntityNameById(name: String, id: Int) {
        withContext(Dispatchers.IO) {
            notebookDao.updateNoteBookEntityNameById(name, id)
        }
    }

    suspend fun deleteNotebookById(id: Int) {
        withContext(Dispatchers.IO) {
            notebookDao.deleteNotebookById(id)
        }
    }

    suspend fun deleteNoteBooks(notebooks: List<Int>){
        withContext(Dispatchers.IO) {
            try {
                notebookDao.deleteNoteBooks(notebooks)
            } catch (e: Exception) {
                Log.e("DeleteNoteBooks", "Failed to delete notebooks: ${e.message}")
                // Optionally, handle the exception further
            }
        }
    }

//    suspend fun deleteNotebooksByIds(ids: List<String>) {
//        // If using Room:
//        noteBookDao.deleteNotebooks(ids)
//
//        // Or, if using a network API:
//        apiService.deleteNotebooks(ids)
//    }

    suspend fun deleteNotebook(notebook: NoteBookEntity) {
        withContext(Dispatchers.IO) {
            notebookDao.deleteNotebook(notebook)
        }
    }

    suspend fun getAllNotebooks(): List<NoteBookEntity> {
        return withContext(Dispatchers.IO) {
            notebookDao.getAllNotebooks()
        }
    }

    suspend fun getAllNotebooksWithTotals(): List<NoteBookEntity> =
        withContext(Dispatchers.IO) {
            val notebooks = notebookDao.getAllNotebooks()
            return@withContext notebooks.map { notebook ->
                val totals = notebookDao.getTotalsForNoteBook(notebook.id)
                notebook.copy(
                    totalOwe = totals.totalOwe,
                    totalCollect = totals.totalCollect
                )
            }
        }


    // provide total for particular noteBook
    suspend fun getTotalsForNoteBook(noteBookId: Int): Totals {
        return withContext(Dispatchers.IO) {
            notebookDao.getTotalsForNoteBook(noteBookId)
        }
    }
}
