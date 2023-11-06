package com.kotlin.boardproject.domain.user.dto

import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.global.enums.Role
import java.util.UUID

data class UserInfoDto(
    val id: UUID,
    val nickname: String,
    val email: String,
    val role: Role,
    val accessToken: String?,
    val refreshToken: String?,
) {
    constructor(user: User, accessToken: String? = null, refreshToken: String? = null) : this(
        user.id!!,
        user.nickname,
        user.email,
        user.role,
        accessToken,
        refreshToken,
    )
}
