package com.littlepay.payquick.domain.repository

import com.littlepay.payquick.domain.model.TransactionsList

interface TransactionRepository {
    suspend fun getTransactions(token: String, page: Int): Result<TransactionsList>
}
