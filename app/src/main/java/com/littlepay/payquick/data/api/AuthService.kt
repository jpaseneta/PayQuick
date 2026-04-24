package com.littlepay.payquick.data.api

import com.littlepay.payquick.data.model.LoginRequest
import com.littlepay.payquick.data.model.LoginResponseDto
import com.littlepay.payquick.data.model.RefreshTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {

    @POST("api/v1/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponseDto>

    @POST("api/v1/token/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<LoginResponseDto>
}
