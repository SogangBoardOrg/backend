package com.kotlin.boardproject.domain.post

import com.kotlin.boardproject.domain.comment.dto.read.CommentsByPostIdResponseDto
import com.kotlin.boardproject.domain.post.dto.black.BlackPostRequestDto
import com.kotlin.boardproject.domain.post.dto.black.BlackPostResponseDto
import com.kotlin.boardproject.domain.post.dto.create.CreatePostRequestDto
import com.kotlin.boardproject.domain.post.dto.create.CreatePostResponseDto
import com.kotlin.boardproject.domain.post.dto.delete.DeletePostResponseDto
import com.kotlin.boardproject.domain.post.dto.edit.EditPostRequestDto
import com.kotlin.boardproject.domain.post.dto.edit.EditPostResponseDto
import com.kotlin.boardproject.domain.post.dto.like.CancelLikePostResponseDto
import com.kotlin.boardproject.domain.post.dto.like.LikePostResponseDto
import com.kotlin.boardproject.domain.post.dto.read.OnePostResponseDto
import com.kotlin.boardproject.domain.post.dto.read.PostByQueryRequestDto
import com.kotlin.boardproject.domain.post.dto.read.PostByQueryResponseDto
import com.kotlin.boardproject.domain.post.dto.scrap.CancelScrapPostResponseDto
import com.kotlin.boardproject.domain.post.dto.scrap.ScrapPostResponseDto
import com.kotlin.boardproject.domain.post.service.PostService
import com.kotlin.boardproject.global.annotation.LoginUser
import com.kotlin.boardproject.global.dto.ApiResponse
import com.kotlin.boardproject.global.enums.PostType
import com.kotlin.boardproject.global.util.log
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
    fun createPost(
        @LoginUser loginUser: User,
        @RequestBody @Valid
        createPostRequestDto: CreatePostRequestDto,
    ): ApiResponse<CreatePostResponseDto> {
        // TODO: list에 빈 문자열 들어오면 validation이 안된다.
        val responseDto = postService.createPost(loginUser.username, createPostRequestDto)
        return ApiResponse.success(responseDto)
    }

    @GetMapping("/query")
    fun findPostByQuery(
        @RequestParam("title", required = false) title: String?,
        @RequestParam("content", required = false) content: String?,
        @RequestParam("writer-name", required = false) writerName: String?,
        @RequestParam("course-id", required = false) courseId: Long?,
        @RequestParam("post-type", required = true) postType: PostType,
        pageable: Pageable,
        principal: Principal?,
    ): ApiResponse<PostByQueryResponseDto> {
        log.info("username: ${principal?.name}")

        val data = postService.findPostByQuery(
            principal?.name,
            pageable,
            PostByQueryRequestDto(
                title,
                content,
                writerName,
                courseId,
                postType,
            ),
        )
        return ApiResponse.success(data)
    }

    @GetMapping("/{postId}")
    fun findOnePost(
        @PathVariable @Positive
        postId: Long,
        principal: Principal?,
    ): ApiResponse<OnePostResponseDto> {
        log.info("username: ${principal?.name}")

        val data = postService.findOnePost(principal?.name, postId) // post 객체 반환
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
    fun editPost(
        @LoginUser loginUser: User,
        @PathVariable @Positive
        postId: Long,
        @RequestBody @Valid
        editPostRequestDto: EditPostRequestDto,
    ): ApiResponse<EditPostResponseDto> {
        val data = postService.editPost(loginUser.username, postId, editPostRequestDto) // post 객체 반환
        return ApiResponse.success(data)
    }

    @DeleteMapping("/{postId}")
    fun deletePost(
        @LoginUser loginUser: User,
        @PathVariable @Positive
        postId: Long,
    ): ApiResponse<DeletePostResponseDto> {
        val data = postService.deletePost(loginUser.username, postId) // post 객체 반환
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
