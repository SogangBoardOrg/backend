package com.kotlin.boardproject.controller

import com.kotlin.boardproject.auth.LoginUser
import com.kotlin.boardproject.dto.comment.CreateCommentRequestDto
import com.kotlin.boardproject.dto.comment.CreateCommentResponseDto
import com.kotlin.boardproject.dto.comment.DeleteCommentResponseDto
import com.kotlin.boardproject.dto.common.ApiResponse
import com.kotlin.boardproject.service.CommentService
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/comment")
class CommentController(
    private val commentService: CommentService,
) {

    @PostMapping("")
    fun createComment(
        @LoginUser loginUser: User,
        @RequestBody createCommentRequestDto: CreateCommentRequestDto,
    ): ApiResponse<CreateCommentResponseDto> {
        val responseDto = commentService.createComment(loginUser.username, createCommentRequestDto)

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
}
