package com.kotlin.boardproject.controller

import com.kotlin.boardproject.auth.LoginUser
import com.kotlin.boardproject.dto.CreateCommentRequestDto
import com.kotlin.boardproject.dto.CreateCommentResponseDto
import com.kotlin.boardproject.dto.common.ApiResponse
import com.kotlin.boardproject.service.CommentService
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}
