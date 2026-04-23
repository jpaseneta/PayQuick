package com.littlepay.payquick.data.repository

import com.littlepay.payquick.data.api.AuthService
import com.littlepay.payquick.data.model.LoginRequest
import com.littlepay.payquick.data.model.toDomain
import com.littlepay.payquick.domain.model.User
import com.littlepay.payquick.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<Pair<User, String>> {
        return try {
            val response = withContext(Dispatchers.IO) {
                authService.login(LoginRequest(email, password))
            }
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.data != null) {
                    Result.success(Pair(body.data.user.toDomain(), body.data.accessToken))
                } else {
                    Result.failure(Exception(body?.message ?: "Unknown error"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
