package com.kotlin.boardproject.domain.my

import com.kotlin.boardproject.domain.auth.service.AuthService
import com.kotlin.boardproject.domain.comment.dto.MyCommentResponseDto
import com.kotlin.boardproject.domain.comment.service.CommentService
import com.kotlin.boardproject.domain.post.dto.read.MyWrittenPostResponseDto
import com.kotlin.boardproject.domain.post.dto.scrap.MyScrapPostResponseDto
import com.kotlin.boardproject.domain.post.service.PostService
import com.kotlin.boardproject.domain.schedule.dto.MyTimeTableListResponseDto
import com.kotlin.boardproject.domain.schedule.service.TimeTableService
import com.kotlin.boardproject.domain.user.dto.UserInfoDto
import com.kotlin.boardproject.global.annotation.LoginUser
import com.kotlin.boardproject.global.dto.ApiResponse
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
    private val timeTableService: TimeTableService,
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

    @GetMapping("/timetable")
    fun myTimeTable(
        @LoginUser loginUser: User,
    ): ApiResponse<MyTimeTableListResponseDto> {
        val data = timeTableService.getMyTimeTableList(loginUser.username)

        return ApiResponse.success(data)
    }
}
