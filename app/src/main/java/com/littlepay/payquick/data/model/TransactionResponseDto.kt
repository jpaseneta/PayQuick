package com.littlepay.payquick.data.model

import com.google.gson.annotations.SerializedName
import com.littlepay.payquick.domain.model.Transaction as DomainTransaction

data class TransactionResponseDto(
    val status: String,
    val data: List<TransactionDto>
)

data class TransactionDto(
    val id: String,
    @SerializedName("amount_in_cents")
    val amountInCents: Long,
    val currency: String,
    val description: String,
    val date: String,
    val status: String
)

fun TransactionDto.toDomain(): DomainTransaction {
    return DomainTransaction(
        id = id,
        amountInCents = amountInCents,
        currency = currency,
        description = description,
        date = date,
        status = status
    )
}
