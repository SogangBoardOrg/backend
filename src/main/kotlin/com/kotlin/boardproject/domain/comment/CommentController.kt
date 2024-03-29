package com.kotlin.boardproject.domain.comment

import com.kotlin.boardproject.domain.comment.dto.black.BlackCommentRequestDto
import com.kotlin.boardproject.domain.comment.dto.black.BlackCommentResponseDto
import com.kotlin.boardproject.domain.comment.dto.create.CreateCommentRequestDto
import com.kotlin.boardproject.domain.comment.dto.create.CreateCommentResponseDto
import com.kotlin.boardproject.domain.comment.dto.delete.DeleteCommentRequestDto
import com.kotlin.boardproject.domain.comment.dto.delete.DeleteCommentResponseDto
import com.kotlin.boardproject.domain.comment.dto.like.CancelLikeCommentResponseDto
import com.kotlin.boardproject.domain.comment.dto.like.LikeCommentResponseDto
import com.kotlin.boardproject.domain.comment.dto.update.UpdateCommentRequestDto
import com.kotlin.boardproject.domain.comment.dto.update.UpdateCommentResponseDto
import com.kotlin.boardproject.domain.comment.service.CommentService
import com.kotlin.boardproject.domain.notification.service.NotificationService
import com.kotlin.boardproject.domain.notification.service.SseService
import com.kotlin.boardproject.global.annotation.LoginUser
import com.kotlin.boardproject.global.dto.ApiResponse
import com.kotlin.boardproject.global.util.log
import org.springframework.security.core.userdetails.User
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.Positive

@Validated
@RestController
@RequestMapping("/api/v1/comment")
class CommentController(
    private val commentService: CommentService,
    private val notificationService: NotificationService,
    private val sseService: SseService,
) {
    // TODO: requestDto에 isAnon 안들어와도 작동이 되는 오류 있음
    @PostMapping("", "/{parentCommentId}")
    fun createComment(
        @LoginUser loginUser: User,
        @PathVariable(required = false) @Positive parentCommentId: Long?,
        @RequestBody @Valid
        createCommentRequestDto: CreateCommentRequestDto,
    ): ApiResponse<CreateCommentResponseDto> {
        log.info(createCommentRequestDto.toString())

        val (data, notificationCreateDto) = commentService.createComment(
            loginUser.username,
            createCommentRequestDto,
            parentCommentId,
        )
        // to url message type -> notification createDto
        // 1. 댓글을 파악한다 -> 댓글에 쿼리를 보내서 parentId가 null이면 일반댓글 아니면 대댓글
        // parentCommentId가 null이면 해당 comment의 글의 주인에게 nofitication을 보낸다.
        // parentCommentId가 null이 아니면 해당 comment의 주인에게 notification을 보낸다.
        // 자신이 작성한 글이나 댓글이면 알림을 보내지 아니한다.
        val notification = notificationService.createNotification(notificationCreateDto)
        // TODO : sseEmiiter 사용하면 다시 켜기
        // sseService.sendEvent(loginUser.username, notification)

        return ApiResponse.success(data)
    }

    @PutMapping("/{commentId}")
    fun updateComment(
        @LoginUser loginUser: User,
        @PathVariable @Positive
        commentId: Long,
        @RequestBody @Valid
        updateCommentRequestDto: UpdateCommentRequestDto,
    ): ApiResponse<UpdateCommentResponseDto> {
        // TODO: isAnon이 null이면 false로 처리되는 오류 있음
        val data = commentService.updateComment(loginUser.username, commentId, updateCommentRequestDto)

        return ApiResponse.success(data)
    }

    @DeleteMapping("/{commentId}")
    fun deleteComment(
        @LoginUser loginUser: User,
        @PathVariable @Positive
        commentId: Long,
        @RequestBody @Valid
        deleteCommentRequestDto: DeleteCommentRequestDto,
    ): ApiResponse<DeleteCommentResponseDto> {
        val data = commentService.deleteComment(loginUser.username, commentId, deleteCommentRequestDto)

        return ApiResponse.success(data)
    }

    @PostMapping("/{commentId}/like")
    fun likeComment(
        @LoginUser loginUser: User,
        @PathVariable @Positive
        commentId: Long,
    ): ApiResponse<LikeCommentResponseDto> {
        val data = commentService.likeComment(loginUser.username, commentId)

        return ApiResponse.success(data)
    }

    @DeleteMapping("/{commentId}/like")
    fun cancelLikeComment(
        @LoginUser loginUser: User,
        @PathVariable @Positive
        commentId: Long,
    ): ApiResponse<CancelLikeCommentResponseDto> {
        val data = commentService.cancelLikeComment(loginUser.username, commentId)

        return ApiResponse.success(data)
    }

    @PostMapping("/{commentId}/black")
    fun blackComment(
        @LoginUser loginUser: User,
        @PathVariable @Positive
        commentId: Long,
        @RequestBody @Valid
        blackCommentRequestDto: BlackCommentRequestDto,
    ): ApiResponse<BlackCommentResponseDto> {
        val data = commentService.blackComment(loginUser.username, commentId, blackCommentRequestDto)

        return ApiResponse.success(data)
    }
}
