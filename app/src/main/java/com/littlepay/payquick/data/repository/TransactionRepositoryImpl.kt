package com.littlepay.payquick.data.repository

import com.littlepay.payquick.data.api.TransactionService
import com.littlepay.payquick.data.model.toDomain
import com.littlepay.payquick.domain.model.AuthorizationException
import com.littlepay.payquick.domain.model.TransactionsList
import com.littlepay.payquick.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionService: TransactionService,
    private val authRepository: AuthRepositoryImpl,
    private val tokenManager: TokenManager
) : TransactionRepository {
    override suspend fun getTransactions(token: String, page: Int): Result<TransactionsList> {
        return try {
            val currentToken = tokenManager.accessToken
            val response = withContext(Dispatchers.IO) {
                delay(2000) // Simulate network delay
                transactionService.getTransactions("Bearer $currentToken", page)
            }

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(
                        TransactionsList(
                            transactions = body.data.map { it.toDomain() },
                            pagination = body.pagination.toDomain()
                        )
                    )
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else if (response.code() == 401) {
                // Token expired, try to refresh
                authRepository.refreshToken()
                    .onSuccess { newToken ->
                        // Retry with new token
                        return getTransactions(newToken, page)
                    }
                    .onFailure {
                        return Result.failure(AuthorizationException("Session expired, please login again"))
                    }
                Result.failure(AuthorizationException("Session expired, please login again"))
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
