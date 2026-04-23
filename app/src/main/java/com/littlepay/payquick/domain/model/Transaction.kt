package com.littlepay.payquick.domain.model

data class Transaction(
    val id: String,
    val amountInCents: Long,
    val currency: String,
    val description: String,
    val date: String,
    val status: String
)
