package com.kotlin.boardproject.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.kotlin.boardproject.common.enums.Role
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserInfoResponseDto(
    val id: UUID,
    val username: String,
    val email: String,
    val role: Role,
    val accessToken: String?,
) {
    constructor(userInfoDto: UserInfoDto) : this(
        userInfoDto.id,
        userInfoDto.username,
        userInfoDto.email,
        userInfoDto.role,
        userInfoDto.accessToken,
    )
}
