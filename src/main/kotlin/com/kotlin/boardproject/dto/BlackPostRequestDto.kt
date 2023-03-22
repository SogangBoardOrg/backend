package com.kotlin.boardproject.dto

import com.kotlin.boardproject.common.enums.BlackReason

data class BlackPostRequestDto(
    val blackReason: BlackReason,
)
