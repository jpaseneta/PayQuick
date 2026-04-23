package com.littlepay.payquick.data.api

import com.littlepay.payquick.data.model.LoginRequest
import com.littlepay.payquick.data.model.LoginResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("v1/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponseDto>
}
