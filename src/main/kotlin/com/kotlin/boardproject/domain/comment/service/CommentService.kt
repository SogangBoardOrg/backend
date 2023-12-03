package com.kotlin.boardproject.domain.comment.service

import com.kotlin.boardproject.domain.comment.dto.black.BlackCommentRequestDto
import com.kotlin.boardproject.domain.comment.dto.black.BlackCommentResponseDto
import com.kotlin.boardproject.domain.comment.dto.create.CreateCommentRequestDto
import com.kotlin.boardproject.domain.comment.dto.create.CreateCommentResponseDto
import com.kotlin.boardproject.domain.comment.dto.delete.DeleteCommentRequestDto
import com.kotlin.boardproject.domain.comment.dto.delete.DeleteCommentResponseDto
import com.kotlin.boardproject.domain.comment.dto.like.CancelLikeCommentResponseDto
import com.kotlin.boardproject.domain.comment.dto.like.LikeCommentResponseDto
import com.kotlin.boardproject.domain.comment.dto.read.MyCommentResponseDto
import com.kotlin.boardproject.domain.comment.dto.update.UpdateCommentRequestDto
import com.kotlin.boardproject.domain.comment.dto.update.UpdateCommentResponseDto
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
