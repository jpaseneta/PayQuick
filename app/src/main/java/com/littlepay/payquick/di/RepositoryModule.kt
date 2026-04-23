package com.littlepay.payquick.di

import com.littlepay.payquick.data.repository.AuthRepositoryImpl
import com.littlepay.payquick.data.repository.TransactionRepositoryImpl
import com.littlepay.payquick.domain.repository.AuthRepository
import com.littlepay.payquick.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl
    ): TransactionRepository
}
