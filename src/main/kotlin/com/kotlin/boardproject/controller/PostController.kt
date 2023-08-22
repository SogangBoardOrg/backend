package com.kotlin.boardproject.controller

import com.kotlin.boardproject.auth.LoginUser
import com.kotlin.boardproject.common.enums.NormalType
import com.kotlin.boardproject.common.util.log
import com.kotlin.boardproject.dto.CommentsByPostIdResponseDto
import com.kotlin.boardproject.dto.common.ApiResponse
import com.kotlin.boardproject.dto.post.*
import com.kotlin.boardproject.dto.post.normalpost.*
import com.kotlin.boardproject.service.PostService
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.User
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid
import javax.validation.constraints.Positive

@Validated
@RestController
@RequestMapping("/api/v1/post")
class PostController(
    private val postService: PostService,
) {
    // TODO: newbie이면 글 쓰기가 안됨 -> security config
    @PostMapping("")
    fun createNormalPost(
        @LoginUser loginUser: User,
        @RequestBody @Valid
        createNormalPostRequestDto: CreateNormalPostRequestDto,
    ): ApiResponse<CreateNormalPostResponseDto> {
        // TODO: list에 빈 문자열 들어오면 validation이 안된다.
        val responseDto = postService.createNormalPost(loginUser.username, createNormalPostRequestDto)
        return ApiResponse.success(responseDto)
    }

    @GetMapping("/query")
    fun findNormalPostByQuery(
        @RequestParam("title", required = false) title: String?,
        @RequestParam("content", required = false) content: String?,
        @RequestParam("writer-name", required = false) writerName: String?,
        @RequestParam("normal-type", required = true) normalType: NormalType,
        pageable: Pageable,
        principal: Principal?,
    ): ApiResponse<NormalPostByQueryResponseDto> {
        log.info("username: ${principal?.name}")

        val data = postService.findNormalPostByQuery(
            principal?.name,
            pageable,
            FindNormalPostByQueryRequestDto(title, content, writerName, normalType),
        )
        return ApiResponse.success(data)
    }

    @GetMapping("/{postId}")
    fun findOneNormalPost(
        @PathVariable @Positive
        postId: Long,
        principal: Principal?,
    ): ApiResponse<OneNormalPostResponseDto> {
        log.info("username: ${principal?.name}")

        val data = postService.findOneNormalPost(principal?.name, postId) // post 객체 반환
        return ApiResponse.success(data)
    }

    @GetMapping("/{postId}/comments")
    fun findCommentsByPostId(
        @PathVariable @Positive
        postId: Long,
        pageable: Pageable,
        principal: Principal?,
    ): ApiResponse<CommentsByPostIdResponseDto> {
        log.info("username: ${principal?.name}")

        val data = postService.findCommentsByPostId(principal?.name, postId)
        return ApiResponse.success(data)
    }

    @PutMapping("/{postId}")
    fun editNormalPost(
        @LoginUser loginUser: User,
        @PathVariable @Positive
        postId: Long,
        @RequestBody @Valid
        editNormalPostRequestDto: EditNormalPostRequestDto,
    ): ApiResponse<EditNormalPostResponseDto> {
        val data = postService.editNormalPost(loginUser.username, postId, editNormalPostRequestDto) // post 객체 반환
        return ApiResponse.success(data)
    }

    @DeleteMapping("/{postId}")
    fun deleteNormalPost(
        @LoginUser loginUser: User,
        @PathVariable @Positive
        postId: Long,
    ): ApiResponse<DeleteNormalPostResponseDto> {
        val data = postService.deleteNormalPost(loginUser.username, postId) // post 객체 반환
        return ApiResponse.success(data)
    }

    @PostMapping("/black/{postId}")
    fun blackPost(
        @LoginUser loginUser: User,
        @PathVariable @Positive
        postId: Long,
        @RequestBody @Valid
        blackPostRequestDto: BlackPostRequestDto,
    ): ApiResponse<BlackPostResponseDto> {
        val data = postService.blackPost(loginUser.username, postId, blackPostRequestDto)
        return ApiResponse.success(data)
    }

    @PostMapping("/like/{postId}")
    fun likePost(
        @LoginUser loginUser: User,
        @PathVariable @Positive
        postId: Long,
    ): ApiResponse<LikePostResponseDto> {
        val data = postService.likePost(loginUser.username, postId)
        return ApiResponse.success(data)
    }

    @DeleteMapping("/like/{postId}")
    fun cancelLikePost(
        @LoginUser loginUser: User,
        @PathVariable @Positive
        postId: Long,
    ): ApiResponse<CancelLikePostResponseDto> {
        val data = postService.cancelLikePost(loginUser.username, postId)
        return ApiResponse.success(data)
    }

    @PostMapping("/scrap/{postId}")
    fun scrapPost(
        @LoginUser loginUser: User,
        @PathVariable @Positive
        postId: Long,
    ): ApiResponse<ScrapPostResponseDto> {
        val data = postService.scrapPost(loginUser.username, postId)
        return ApiResponse.success(data)
    }

    @DeleteMapping("/scrap/{postId}")
    fun cancelScrapPost(
        @LoginUser loginUser: User,
        @PathVariable @Positive
        postId: Long,
    ): ApiResponse<CancelScrapPostResponseDto> {
        val data = postService.cancelScrapPost(loginUser.username, postId)
        return ApiResponse.success(data)
    }
}
