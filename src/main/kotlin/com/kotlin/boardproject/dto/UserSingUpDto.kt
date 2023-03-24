package com.kotlin.boardproject.dto

import com.kotlin.boardproject.auth.ProviderType
import com.kotlin.boardproject.model.User

data class UserSignUpDto(
    val email: String,
    val username: String,
    val password: String,
) {
    fun toUser(): User {
        return User(
            email = email,
            nickname = username,
            password = password,
            providerType = ProviderType.LOCAL,
        )
    }
}
