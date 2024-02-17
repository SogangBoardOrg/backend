package com.kotlin.boardproject.domain.auth.dto

import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.global.enums.Role
import java.util.UUID

data class UserInfoDtoWithProfile(
    val id: UUID,
    val nickname: String,
    val email: String,
    val role: Role,
    val profileImageUrl: String?,
) {
    constructor(user: User) : this(
        user.id!!,
        user.nickname,
        user.email,
        user.role,
        user.profileImageUrl,
    )
}
