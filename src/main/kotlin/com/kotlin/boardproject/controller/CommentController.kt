package com.kotlin.boardproject.controller

import com.kotlin.boardproject.auth.LoginUser
import com.kotlin.boardproject.common.util.log
import com.kotlin.boardproject.dto.FindMyCommentResponseDto
import com.kotlin.boardproject.dto.comment.*
import com.kotlin.boardproject.dto.common.ApiResponse
import com.kotlin.boardproject.service.CommentService
import com.kotlin.boardproject.service.NotificationService
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/comment")
class CommentController(
    private val commentService: CommentService,
    private val notificationService: NotificationService,
) {

    @GetMapping("/mycomment")
    fun myComment(
        @LoginUser loginUser: User,
        pageable: Pageable,
    ): ApiResponse<FindMyCommentResponseDto> {
        val data = commentService.findMyComment(loginUser.username, pageable)

        return ApiResponse.success(data)
    }

    @PostMapping("", "/{parentCommentId}")
    fun createComment(
        @LoginUser loginUser: User,
        @PathVariable("parentCommentId", required = false) parentCommentId: Long?,
        @RequestBody createCommentRequestDto: CreateCommentRequestDto,
    ): ApiResponse<CreateCommentResponseDto> {
        val data = commentService.createComment(
            loginUser.username,
            createCommentRequestDto,
            parentCommentId,
        )
        log.info(data.toString())
        log.info("data to string")
        // TODO: 여기에 댓글 생성 시 알림 기능 추가
        // 1. 댓글을 파악한다 -> 댓글에 쿼리를 보내서 parentId가 null이면 일반댓글 아니면 대댓글
        // parentCommentId가 null이면 해당 comment의 글의 주인에게 nofitication을 보낸다.
        // parentCommentId가 null이 아니면 해당 comment의 주인에게 notification을 보낸다.
        // 자신이 작성한 글이나 댓글이면 알림을 보내지 아니한다.
        notificationService.createNotification(loginUser.username, data)

        return ApiResponse.success(data)
    }

    @PutMapping("/{commentId}")
    fun updateComment(
        @LoginUser loginUser: User,
        @PathVariable commentId: Long,
        @RequestBody updateCommentRequestDto: UpdateCommentRequestDto,
    ): ApiResponse<UpdateCommentResponseDto> {
        val data = commentService.updateComment(loginUser.username, commentId, updateCommentRequestDto)

        return ApiResponse.success(data)
    }

    @PostMapping("/like/{commentId}")
    fun likeComment(
        @LoginUser loginUser: User,
        @PathVariable("commentId") commentId: Long,
    ): ApiResponse<LikeCommentResponseDto> {
        val data = commentService.likeComment(loginUser.username, commentId)

        return ApiResponse.success(data)
    }

    @DeleteMapping("/like/{commentId}")
    fun cancelLikeComment(
        @LoginUser loginUser: User,
        @PathVariable("commentId") commentId: Long,
    ): ApiResponse<CancelLikeCommentResponseDto> {
        val data = commentService.cancelLikeComment(loginUser.username, commentId)

        return ApiResponse.success(data)
    }

    @DeleteMapping("/{commentId}")
    fun deleteComment(
        @LoginUser loginUser: User,
        @PathVariable commentId: Long,
    ): ApiResponse<DeleteCommentResponseDto> {
        val data = commentService.deleteComment(loginUser.username, commentId)

        return ApiResponse.success(data)
    }

    @PostMapping("/black/{commentId}")
    fun blackComment(
        @LoginUser loginUser: User,
        @PathVariable("commentId") commentId: Long,
        @RequestBody blackCommentRequestDto: BlackCommentRequestDto,
    ): ApiResponse<BlackCommentResponseDto> {
        val data = commentService.blackComment(loginUser.username, commentId, blackCommentRequestDto)

        return ApiResponse.success(data)
    }
}
