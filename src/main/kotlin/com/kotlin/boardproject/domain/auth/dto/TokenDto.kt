package com.kotlin.boardproject.domain.auth.dto

data class TokenDto(
    val accessToken: String,
    val refreshToken: String?,
)
