package dev.board.boardprojectproto.dto

import dev.board.boardprojectproto.auth.ProviderType
import dev.board.boardprojectproto.model.User

data class UserSignUpDto(
    val email: String,
    val username: String,
    val password: String,
) {
    fun toUser(): User {
        return User(
            email = email,
            username = username,
            password = password,
            providerType = ProviderType.LOCAL,
        )
    }
}
