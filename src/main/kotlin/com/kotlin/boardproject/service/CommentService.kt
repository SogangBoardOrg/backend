package com.kotlin.boardproject.service

import com.kotlin.boardproject.dto.comment.*
import com.kotlin.boardproject.dto.post.CancelLikePostResponseDto

interface CommentService {
    fun createComment(
        username: String,
        createCommentRequestDto: CreateCommentRequestDto,
    ): CreateCommentResponseDto

    fun deleteComment(
        username: String,
        commentId: Long,
    ): DeleteCommentResponseDto

    fun updateComment(
        username: String,
        commentId: Long,
        updateCommentRequestDto: UpdateCommentRequestDto,
    ): UpdateCommentResponseDto

    fun likeComment(
        username: String,
        commentId: Long,
    ): LikeCommentResponseDto

    fun cancelLikeComment(
        username: String,
        commentId: Long,
    ): CancelLikeCommentResponseDto
}
