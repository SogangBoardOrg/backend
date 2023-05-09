package com.kotlin.boardproject.dto

data class TokenDto(
    val accessToken: String,
    val refreshToken: String?,
)