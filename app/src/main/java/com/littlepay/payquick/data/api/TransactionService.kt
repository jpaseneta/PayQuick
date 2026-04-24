package com.littlepay.payquick.data.api

import com.littlepay.payquick.data.model.TransactionResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface TransactionService {
    @GET("api/v1/transactions")
    suspend fun getTransactions(
        @Header("Authorization") token: String,
        @Query("page") page: Int
    ): Response<TransactionResponseDto>
}
