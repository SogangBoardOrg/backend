package com.kotlin.boardproject.domain.post

import com.kotlin.boardproject.domain.comment.dto.CommentsByPostIdResponseDto
import com.kotlin.boardproject.domain.post.dto.BlackPostRequestDto
import com.kotlin.boardproject.domain.post.dto.BlackPostResponseDto
import com.kotlin.boardproject.domain.post.dto.CancelLikePostResponseDto
import com.kotlin.boardproject.domain.post.dto.CancelScrapPostResponseDto
import com.kotlin.boardproject.domain.post.dto.LikePostResponseDto
import com.kotlin.boardproject.domain.post.dto.ScrapPostResponseDto
import com.kotlin.boardproject.domain.post.dto.normalpost.CreateNormalPostRequestDto
import com.kotlin.boardproject.domain.post.dto.normalpost.CreateNormalPostResponseDto
import com.kotlin.boardproject.domain.post.dto.normalpost.DeleteNormalPostResponseDto
import com.kotlin.boardproject.domain.post.dto.normalpost.EditNormalPostRequestDto
import com.kotlin.boardproject.domain.post.dto.normalpost.EditNormalPostResponseDto
import com.kotlin.boardproject.domain.post.dto.normalpost.FindNormalPostByQueryRequestDto
import com.kotlin.boardproject.domain.post.dto.normalpost.NormalPostByQueryResponseDto
import com.kotlin.boardproject.domain.post.dto.normalpost.OneNormalPostResponseDto
import com.kotlin.boardproject.domain.post.service.PostService
import com.kotlin.boardproject.global.annotation.LoginUser
import com.kotlin.boardproject.global.dto.ApiResponse
import com.kotlin.boardproject.global.enums.NormalType
import com.kotlin.boardproject.global.util.log
import java.security.Principal
import javax.validation.Valid
import javax.validation.constraints.Positive
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.User
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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