package com.kotlin.boardproject.controller

import com.kotlin.boardproject.auth.LoginUser
import com.kotlin.boardproject.dto.MyCommentResponseDto
import com.kotlin.boardproject.dto.common.ApiResponse
import com.kotlin.boardproject.service.CommentService
import com.kotlin.boardproject.service.PostService
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.User
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/api/v1/my")
class MyController(
    private val postService: PostService,
    private val commentService: CommentService,
) {
    @GetMapping("/comment")
    fun myComment(
        @LoginUser loginUser: User,
        pageable: Pageable,
    ): ApiResponse<MyCommentResponseDto> {
        val data = commentService.findMyComment(loginUser.username, pageable)

        return ApiResponse.success(data)
    }
}
