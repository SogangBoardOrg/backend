package com.kotlin.boardproject.domain.my

import com.kotlin.boardproject.global.annotation.LoginUser
import com.kotlin.boardproject.domain.user.dto.UserInfoDto
import com.kotlin.boardproject.domain.comment.dto.MyCommentResponseDto
import com.kotlin.boardproject.global.dto.ApiResponse
import com.kotlin.boardproject.domain.post.dto.MyScrapPostResponseDto
import com.kotlin.boardproject.domain.post.dto.MyWrittenPostResponseDto
import com.kotlin.boardproject.domain.auth.service.AuthService
import com.kotlin.boardproject.domain.comment.service.CommentService
import com.kotlin.boardproject.domain.post.service.PostService
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
    private val authService: AuthService,
) {
    @GetMapping("/comment")
    fun myComment(
        @LoginUser loginUser: User,
        pageable: Pageable,
    ): ApiResponse<MyCommentResponseDto> {
        val data = commentService.findMyComment(loginUser.username, pageable)

        return ApiResponse.success(data)
    }

    @GetMapping("/post")
    fun myPost(
        @LoginUser loginUser: User,
        pageable: Pageable,
    ): ApiResponse<MyWrittenPostResponseDto> {
        val data = postService.findMyWrittenPost(loginUser.username, pageable)

        return ApiResponse.success(data)
    }

    @GetMapping("/scrap")
    fun myScrapped(
        @LoginUser loginUser: User,
        pageable: Pageable,
    ): ApiResponse<MyScrapPostResponseDto> {
        val data = postService.findMyScrapPost(loginUser.username, pageable)

        return ApiResponse.success(data)
    }

    @GetMapping("/info")
    fun myInfo(
        @LoginUser loginUser: User,
    ): ApiResponse<UserInfoDto> {
        val data = authService.getUserInfo(loginUser.username)

        return ApiResponse.success(data)
    }
}
