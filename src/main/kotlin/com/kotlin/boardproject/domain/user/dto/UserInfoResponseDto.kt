package com.kotlin.boardproject.domain.user.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserInfoResponseDto(
    val id: UUID,
    val nickname: String,
    val email: String,
    val accessToken: String?,
) {
    constructor(userInfoDto: UserInfoDto) : this(
        userInfoDto.id,
        userInfoDto.nickname,
        userInfoDto.email,
        userInfoDto.accessToken,
    )
}
