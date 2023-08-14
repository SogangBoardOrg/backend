package com.kotlin.boardproject.dto.comment

data class CreateCommentResponseDto(
    val id: Long,
    val postId: Long,
    val content: String,
)
