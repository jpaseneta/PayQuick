package com.littlepay.payquick.data.model

import com.google.gson.annotations.SerializedName
import com.littlepay.payquick.domain.model.Pagination as DomainPagination
import com.littlepay.payquick.domain.model.Transaction as DomainTransaction

data class TransactionResponseDto(
    val status: String,
    val data: List<TransactionDto>,
    val pagination: PaginationDto
)

data class PaginationDto(
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_items")
    val totalItems: Int,
    @SerializedName("items_per_page")
    val itemsPerPage: Int
)

data class TransactionDto(
    val id: String,
    @SerializedName("amount_in_cents")
    val amountInCents: Long,
    val currency: String,
    @SerializedName("type")
    val description: String,
    @SerializedName("created_at")
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

fun PaginationDto.toDomain(): DomainPagination {
    return DomainPagination(
        currentPage = currentPage,
        totalPages = totalPages,
        totalItems = totalItems,
        itemsPerPage = itemsPerPage
    )
}
