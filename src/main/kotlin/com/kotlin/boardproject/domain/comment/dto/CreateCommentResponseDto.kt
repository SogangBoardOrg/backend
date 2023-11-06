package com.kotlin.boardproject.domain.comment.dto

data class CreateCommentResponseDto(
    val id: Long,
    val postId: Long,
    val content: String,
)
