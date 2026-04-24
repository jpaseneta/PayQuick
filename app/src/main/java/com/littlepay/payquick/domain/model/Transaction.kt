package com.littlepay.payquick.domain.model

data class Transaction(
    val id: String,
    val amountInCents: Long,
    val currency: String,
    val description: String,
    val date: String,
    val status: String
)

data class Pagination(
    val currentPage: Int,
    val totalPages: Int,
    val totalItems: Int,
    val itemsPerPage: Int
)

data class TransactionsList(
    val transactions: List<Transaction>,
    val pagination: Pagination
)
