package com.littlepay.payquick.domain.repository

import com.littlepay.payquick.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Pair<User, String>>
}
