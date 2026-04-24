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
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("token_type")
    val tokenType: String,
    val user: UserDto
)

data class UserDto(
    @SerializedName("user_id")
    val id: String,
    @SerializedName("full_name")
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
