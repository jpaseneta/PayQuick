package com.littlepay.payquick.data.repository

import com.littlepay.payquick.data.api.TransactionService
import com.littlepay.payquick.data.model.toDomain
import com.littlepay.payquick.domain.model.Transaction
import com.littlepay.payquick.domain.repository.TransactionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionService: TransactionService
) : TransactionRepository {
    override suspend fun getTransactions(token: String, page: Int): Result<List<Transaction>> {
        return try {
            val response = transactionService.getTransactions("Bearer $token", page)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body.data.map { it.toDomain() })
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
