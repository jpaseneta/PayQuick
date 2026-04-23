package com.littlepay.payquick.domain.repository

import com.littlepay.payquick.domain.model.Transaction

interface TransactionRepository {
    suspend fun getTransactions(token: String, page: Int): Result<List<Transaction>>
}
