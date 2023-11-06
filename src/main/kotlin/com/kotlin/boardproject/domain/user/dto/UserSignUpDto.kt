package com.kotlin.boardproject.domain.user.dto

import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.global.enums.ProviderType

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
