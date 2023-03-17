package com.kotlin.boardproject.controller

import com.kotlin.boardproject.auth.LoginUser
import com.kotlin.boardproject.dto.*
import com.kotlin.boardproject.dto.common.ApiResponse
import com.kotlin.boardproject.service.PostService
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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

    @GetMapping("/postId}")
    fun readOnePost(
        @PathVariable("postId") postId: Long,
    ): ApiResponse<ReadOnePostResponseDto> {
        val postDto = postService.readOnePost(postId) // post 객체 반환
        return ApiResponse.success(postDto)
    }

    @PutMapping("/{postId}")
    fun editPost(
        @LoginUser loginUser: User,
        @PathVariable("postId") postId: Long,
        @RequestBody editPostRequestDto: EditPostRequestDto,
    ): ApiResponse<Long> {
        val id = postService.editPost(loginUser.username, postId, editPostRequestDto) // post 객체 반환
        return ApiResponse.success(id)
    }

//    @GetMapping("free/v/")
//    fun readAllPost(
//    ): ApiResponse<ReadAllPostResponseDto> {
//        val postDto = postService.readOnePost(postId) // post 객체 반환
//        return ApiResponse.success(postDto)
//    }
}
