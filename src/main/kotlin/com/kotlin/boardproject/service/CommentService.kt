package com.kotlin.boardproject.service

import com.kotlin.boardproject.dto.comment.BlackCommentRequestDto
import com.kotlin.boardproject.dto.comment.BlackCommentResponseDto
import com.kotlin.boardproject.dto.comment.CancelLikeCommentResponseDto
import com.kotlin.boardproject.dto.comment.CreateCommentRequestDto
import com.kotlin.boardproject.dto.comment.CreateCommentResponseDto
import com.kotlin.boardproject.dto.comment.DeleteCommentRequestDto
import com.kotlin.boardproject.dto.comment.DeleteCommentResponseDto
import com.kotlin.boardproject.dto.comment.LikeCommentResponseDto
import com.kotlin.boardproject.dto.comment.MyCommentResponseDto
import com.kotlin.boardproject.dto.comment.UpdateCommentRequestDto
import com.kotlin.boardproject.dto.comment.UpdateCommentResponseDto
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
