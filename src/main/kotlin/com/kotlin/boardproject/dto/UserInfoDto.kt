package com.kotlin.boardproject.dto

import com.kotlin.boardproject.common.enums.Role
import com.kotlin.boardproject.model.User
import java.util.*

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
