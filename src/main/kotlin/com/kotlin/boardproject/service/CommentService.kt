package com.kotlin.boardproject.service

import com.kotlin.boardproject.dto.comment.*
import com.kotlin.boardproject.dto.notification.NotificationCreateDto
import org.springframework.data.domain.Pageable

interface CommentService {
    fun createComment(
        username: String,
        createCommentRequestDto: CreateCommentRequestDto,
        parentCommentId: Long?,
    ): Pair<CreateCommentResponseDto, NotificationCreateDto>

    fun deleteComment(
        username: String,
        commentId: Long,
        deleteCommentRequestDto: DeleteCommentRequestDto,
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

    fun findMyComment(
        username: String,
        pageable: Pageable,
    ): MyCommentResponseDto
}
