package com.example.udhari.di

import com.example.udhari.data.dao.FinanceEntityDao
import com.example.udhari.data.dao.NoteBookDao
import com.example.udhari.data.dao.PendingTransactionDao
import com.example.udhari.data.repositories.FinanceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideFinanceRepository(
        noteBookDao: NoteBookDao,
        transactionDao: PendingTransactionDao,
        entityDao: FinanceEntityDao,
    ): FinanceRepository {
        return FinanceRepository(transactionDao, entityDao, noteBookDao)
    }
}
