package com.kotlin.boardproject.controller

import com.kotlin.boardproject.auth.LoginUser
import com.kotlin.boardproject.dto.common.ApiResponse
import com.kotlin.boardproject.dto.post.BlackPostRequestDto
import com.kotlin.boardproject.dto.post.BlackPostResponseDto
import com.kotlin.boardproject.dto.post.CancelLikePostResponseDto
import com.kotlin.boardproject.dto.post.LikePostResponseDto
import com.kotlin.boardproject.dto.post.normalpost.*
import com.kotlin.boardproject.service.PostService
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*

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
        val responseDto = postService.createNormalPost(loginUser.username, createNormalPostRequestDto)
        return ApiResponse.success(responseDto)
    }

    @GetMapping("/{postId}")
    fun findOneNormalPostById(
        @PathVariable("postId") postId: Long,
    ): ApiResponse<OneNormalPostResponseDto> {
        // TODO: 여기는 principal 생성해서 로그인 한 경우는 like 정보를 보내줘야 한다.
        val data = postService.findOneNormalPostById(postId) // post 객체 반환
        return ApiResponse.success(data)
    }

    @PutMapping("/{postId}")
    fun editNormalPost(
        @LoginUser loginUser: User,
        @PathVariable("postId") postId: Long,
        @RequestBody editNormalPostRequestDto: EditNormalPostRequestDto,
    ): ApiResponse<EditNormalPostResponseDto> {
        val data = postService.editNormalPost(loginUser.username, postId, editNormalPostRequestDto) // post 객체 반환
        return ApiResponse.success(data)
    }

    @DeleteMapping("/{postId}")
    fun deleteNormalPost(
        @LoginUser loginUser: User,
        @PathVariable("postId") postId: Long,
    ): ApiResponse<DeleteNormalPostResponseDto> {
        val data = postService.deleteNormalPost(loginUser.username, postId) // post 객체 반환
        return ApiResponse.success(data)
    }

    @PostMapping("/black/{postId}")
    fun blackPost(
        @LoginUser loginUser: User,
        @PathVariable("postId") postId: Long,
        @RequestBody blackPostRequestDto: BlackPostRequestDto,
    ): ApiResponse<BlackPostResponseDto> {
        val data = postService.blackPost(loginUser.username, postId, blackPostRequestDto)
        return ApiResponse.success(data)
    }

    @PostMapping("/like/{postId}")
    fun likePost(
        @LoginUser loginUser: User,
        @PathVariable("postId") postId: Long,
    ): ApiResponse<LikePostResponseDto> {
        val data = postService.likePost(loginUser.username, postId)
        return ApiResponse.success(data)
    }

    @DeleteMapping("/like/{postId}")
    fun cancelLikePost(
        @LoginUser loginUser: User,
        @PathVariable("postId") postId: Long,
    ): ApiResponse<CancelLikePostResponseDto> {
        val data = postService.cancelLikePost(loginUser.username, postId)
        return ApiResponse.success(data)
    }

//    @GetMapping("free/v/")
//    fun readAllPost(
//    ): ApiResponse<ReadAllPostResponseDto> {
//        val postDto = postService.readOnePost(postId) // post 객체 반환
//        return ApiResponse.success(postDto)
//    }
}
