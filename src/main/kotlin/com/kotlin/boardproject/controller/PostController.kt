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
    fun createNormalPost(
        @LoginUser loginUser: User,
        @RequestBody createNormalPostRequestDto: CreateNormalPostRequestDto,
    ): ApiResponse<CreateNormalPostResponseDto> {
        postService.createNormalPost(loginUser.username, createNormalPostRequestDto)
        return ApiResponse.success(CreateNormalPostResponseDto(1L))
    }

    @GetMapping("/postId}")
    fun readOneNormalPost(
        @PathVariable("postId") postId: Long,
    ): ApiResponse<ReadOneNormalPostResponseDto> {
        val postDto = postService.readOneNormalPost(postId) // post 객체 반환
        return ApiResponse.success(postDto)
    }

    @PutMapping("/{postId}")
    fun editNormalPost(
        @LoginUser loginUser: User,
        @PathVariable("postId") postId: Long,
        @RequestBody editNormalPostRequestDto: EditNormalPostRequestDto,
    ): ApiResponse<Long> {
        val id = postService.editNormalPost(loginUser.username, postId, editNormalPostRequestDto) // post 객체 반환
        return ApiResponse.success(id)
    }

//    @GetMapping("free/v/")
//    fun readAllPost(
//    ): ApiResponse<ReadAllPostResponseDto> {
//        val postDto = postService.readOnePost(postId) // post 객체 반환
//        return ApiResponse.success(postDto)
//    }
}
