package com.kotlin.boardproject.controller

import com.kotlin.boardproject.auth.LoginUser
import com.kotlin.boardproject.dto.CreatePostRequestDto
import com.kotlin.boardproject.dto.CreatePostResponseDto
import com.kotlin.boardproject.dto.ReadAllPostResponseDto
import com.kotlin.boardproject.dto.ReadOnePostResponseDto
import com.kotlin.boardproject.dto.common.ApiResponse
import com.kotlin.boardproject.service.PostService
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/post")
class PostController(
    private val postService: PostService,
) {

    @PostMapping("/create")
    fun createPost(
        @LoginUser loginUser: User,
        @RequestBody createPostRequestDto: CreatePostRequestDto,
    ): ApiResponse<CreatePostResponseDto> {
        postService.createPost(loginUser.username, createPostRequestDto)
        return ApiResponse.success(CreatePostResponseDto(1L))
    }

    @GetMapping("free/v/{postId}")
    fun readOnePost(
        @PathVariable("postId") postId: Long,
    ): ApiResponse<ReadOnePostResponseDto> {
        val postDto = postService.readOnePost(postId) // post 객체 반환
        return ApiResponse.success(postDto)
    }

//    @GetMapping("free/v/")
//    fun readAllPost(
//    ): ApiResponse<ReadAllPostResponseDto> {
//        val postDto = postService.readOnePost(postId) // post 객체 반환
//        return ApiResponse.success(postDto)
//    }

}
