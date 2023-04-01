package com.kotlin.boardproject.service

import com.kotlin.boardproject.dto.comment.CreateCommentRequestDto
import com.kotlin.boardproject.dto.comment.CreateCommentResponseDto
import com.kotlin.boardproject.dto.comment.DeleteCommentResponseDto

interface CommentService {
    fun createComment(
        username: String,
        createCommentRequestDto: CreateCommentRequestDto,
    ): CreateCommentResponseDto

    fun deleteComment(
        username: String,
        commentId: Long,
    ): DeleteCommentResponseDto
}
