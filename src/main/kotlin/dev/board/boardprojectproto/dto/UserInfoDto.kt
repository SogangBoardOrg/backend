package dev.board.boardprojectproto.dto

import dev.board.boardprojectproto.common.enums.Role
import dev.board.boardprojectproto.model.User
import java.util.*

data class UserInfoDto(
    val id: UUID,
    val username: String,
    val email: String,
    val role: Role,
    val accessToken: String?,
    val refreshToken: String?,
) {
    constructor(user: User, accessToken: String? = null, refreshToken: String? = null) : this(
        user.id!!,
        user.username,
        user.email,
        user.role,
        accessToken,
        refreshToken,
    )
}
