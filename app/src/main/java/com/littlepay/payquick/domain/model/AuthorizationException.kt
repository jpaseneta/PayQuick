package com.littlepay.payquick.domain.model

data class AuthorizationException(override val message: String) : Exception(message)