package com.kotlin.boardproject.service

import com.kotlin.boardproject.dto.comment.*

interface CommentService {
    fun createComment(
        username: String,
        createCommentRequestDto: CreateCommentRequestDto,
        parentCommentId: Long?,
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

    fun blackComment(
        username: String,
        commentId: Long,
        blackCommentRequestDto: BlackCommentRequestDto,
    ): BlackCommentResponseDto
}
