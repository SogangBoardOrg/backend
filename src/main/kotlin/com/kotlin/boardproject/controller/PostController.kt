package com.kotlin.boardproject.controller

import com.kotlin.boardproject.auth.LoginUser
import com.kotlin.boardproject.common.enums.NormalType
import com.kotlin.boardproject.common.util.log
import com.kotlin.boardproject.dto.PostSearchDto
import com.kotlin.boardproject.dto.common.ApiResponse
import com.kotlin.boardproject.dto.post.*
import com.kotlin.boardproject.dto.post.normalpost.*
import com.kotlin.boardproject.service.PostService
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/v1/post")
class PostController(
    private val postService: PostService,
) {

    // TODO: 로그인을 요구하자 / 그렇게해서 자기가 쓴글은 자기가 썻다고 표시가 가능하게
    @GetMapping("/query")
    fun testPagination(
        @RequestParam("title", required = false) title: String?,
        @RequestParam("content", required = false) content: String?,
        @RequestParam("writer-name", required = false) writerName: String?,
        @RequestParam("normal-type", required = true) normalType: NormalType,
        pageable: Pageable,
        principal: Principal?,
    ): ApiResponse<NormalPostSearchResponseDto> {
        val postSearchDto = PostSearchDto(title, content, writerName, normalType)

        val data = postService.findNormalPostByQuery(principal?.name, pageable, postSearchDto)
        return ApiResponse.success(data)
    }

    @PostMapping("")
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
        principal: Principal?,
    ): ApiResponse<OneNormalPostResponseDto> {
        log.info("username: ${principal?.name}")

        val data = postService.findOneNormalPostById(principal?.name, postId) // post 객체 반환
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

    @PostMapping("/scrap/{postId}")
    fun scrapePost(
        @LoginUser loginUser: User,
        @PathVariable("postId") postId: Long,
    ): ApiResponse<ScrapPostResponseDto> {
        val data = postService.scrapPost(loginUser.username, postId)
        return ApiResponse.success(data)
    }

    @DeleteMapping("/scrap/{postId}")
    fun cancelScrapePost(
        @LoginUser loginUser: User,
        @PathVariable("postId") postId: Long,
    ): ApiResponse<CancelScrapPostResponseDto> {
        val data = postService.cancelScrapPost(loginUser.username, postId)
        return ApiResponse.success(data)
    }

//    @GetMapping("free/v/")
//    fun readAllPost(
//    ): ApiResponse<ReadAllPostResponseDto> {
//        val postDto = postService.readOnePost(postId) // post 객체 반환
//        return ApiResponse.success(postDto)
//    }
}
