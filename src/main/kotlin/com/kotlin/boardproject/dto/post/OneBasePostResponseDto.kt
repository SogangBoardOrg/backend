package com.kotlin.boardproject.dto.post

import java.time.LocalDateTime

data class OneBasePostResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val createdTime: LocalDateTime,
    val lastModifiedTime: LocalDateTime?,
)
