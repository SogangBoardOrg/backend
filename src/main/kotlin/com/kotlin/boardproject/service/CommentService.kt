package com.kotlin.boardproject.service

import com.kotlin.boardproject.dto.CreateCommentRequestDto
import com.kotlin.boardproject.dto.CreateCommentResponseDto

interface CommentService {
    fun createComment(
        username: String,
        createCommentRequestDto: CreateCommentRequestDto,
    ): CreateCommentResponseDto
}
