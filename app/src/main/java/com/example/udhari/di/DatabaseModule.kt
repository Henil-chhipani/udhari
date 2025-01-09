package com.example.udhari.di


import android.content.Context
import androidx.room.Room
import com.example.udhari.data.TransactionDatabase
import com.example.udhari.data.dao.PendingTransactionDao
import com.example.udhari.data.dao.FinanceEntityDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext app: Context): TransactionDatabase {
        return Room.databaseBuilder(app, TransactionDatabase::class.java, "transactions_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providePendingTransactionDao(db: TransactionDatabase) = db.pendingTransactionDao()


    @Provides
    fun provideFinanceEntityDao(db: TransactionDatabase) = db.financeEntityDao()

    @Provides
    fun provideNoteBookDao(db: TransactionDatabase) = db.noteBookDao()
}
