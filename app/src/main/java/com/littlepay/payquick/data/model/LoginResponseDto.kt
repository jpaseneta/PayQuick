package com.littlepay.payquick.data.model

import com.google.gson.annotations.SerializedName
import com.littlepay.payquick.domain.model.User as DomainUser

data class LoginResponseDto(
    val status: String,
    val message: String,
    val data: LoginDataDto?
)

data class LoginDataDto(
    @SerializedName("access_token")
    val accessToken: String,
    val user: UserDto
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String
)

fun UserDto.toDomain(): DomainUser {
    return DomainUser(
        id = id,
        name = name,
        email = email
    )
}
