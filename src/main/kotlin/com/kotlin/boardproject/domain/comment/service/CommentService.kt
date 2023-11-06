package com.kotlin.boardproject.domain.comment.service

import com.kotlin.boardproject.domain.comment.dto.BlackCommentRequestDto
import com.kotlin.boardproject.domain.comment.dto.BlackCommentResponseDto
import com.kotlin.boardproject.domain.comment.dto.CancelLikeCommentResponseDto
import com.kotlin.boardproject.domain.comment.dto.CreateCommentRequestDto
import com.kotlin.boardproject.domain.comment.dto.CreateCommentResponseDto
import com.kotlin.boardproject.domain.comment.dto.DeleteCommentRequestDto
import com.kotlin.boardproject.domain.comment.dto.DeleteCommentResponseDto
import com.kotlin.boardproject.domain.comment.dto.LikeCommentResponseDto
import com.kotlin.boardproject.domain.comment.dto.MyCommentResponseDto
import com.kotlin.boardproject.domain.comment.dto.UpdateCommentRequestDto
import com.kotlin.boardproject.domain.comment.dto.UpdateCommentResponseDto
import com.kotlin.boardproject.domain.notification.dto.NotificationCreateDto
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
