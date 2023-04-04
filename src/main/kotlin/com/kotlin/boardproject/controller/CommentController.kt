package com.kotlin.boardproject.controller

import com.kotlin.boardproject.auth.LoginUser
import com.kotlin.boardproject.dto.comment.*
import com.kotlin.boardproject.dto.common.ApiResponse
import com.kotlin.boardproject.service.CommentService
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/comment")
class CommentController(
    private val commentService: CommentService,
) {

    @PostMapping("/{parentCommentId}", "")
    fun createComment(
        @LoginUser loginUser: User,
        @PathVariable("parentCommentId", required = false) parentCommentId: Long?,
        @RequestBody createCommentRequestDto: CreateCommentRequestDto,
    ): ApiResponse<CreateCommentResponseDto> {
        // path variable requried를 false로 설정해서 댓글과 대댓글 구분하자
        // log.info(parentCommentId.toString())

        val responseDto = commentService.createComment(
            loginUser.username,
            createCommentRequestDto,
            parentCommentId,
        )

        return ApiResponse.success(responseDto)
    }

    @PutMapping("/{commentId}")
    fun updateComment(
        @LoginUser loginUser: User,
        @PathVariable commentId: Long,
        @RequestBody updateCommentRequestDto: UpdateCommentRequestDto,
    ): ApiResponse<UpdateCommentResponseDto> {
        val responseDto = commentService.updateComment(loginUser.username, commentId, updateCommentRequestDto)

        return ApiResponse.success(responseDto)
    }

    @PostMapping("/like/{commentId}")
    fun likeComment(
        @LoginUser loginUser: User,
        @PathVariable("commentId") commentId: Long,
    ): ApiResponse<LikeCommentResponseDto> {
        val responseDto = commentService.likeComment(loginUser.username, commentId)

        return ApiResponse.success(responseDto)
    }

    @DeleteMapping("/like/{commentId}")
    fun cancelLikeComment(
        @LoginUser loginUser: User,
        @PathVariable("commentId") commentId: Long,
    ): ApiResponse<CancelLikeCommentResponseDto> {
        val responseDto = commentService.cancelLikeComment(loginUser.username, commentId)

        return ApiResponse.success(responseDto)
    }

    @DeleteMapping("/{commentId}")
    fun deleteComment(
        @LoginUser loginUser: User,
        @PathVariable commentId: Long,
    ): ApiResponse<DeleteCommentResponseDto> {
        val responseDto = commentService.deleteComment(loginUser.username, commentId)

        return ApiResponse.success(responseDto)
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
