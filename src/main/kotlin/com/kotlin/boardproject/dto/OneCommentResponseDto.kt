package com.kotlin.boardproject.dto

import java.time.LocalDateTime

data class OneCommentResponseDto(
    val id: Long,
    val postId: Long,
    val content: String,
    val createdTime: LocalDateTime,
    val lastModifiedTime: LocalDateTime,
)
