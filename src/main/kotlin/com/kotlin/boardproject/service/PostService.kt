package com.kotlin.boardproject.service

import com.kotlin.boardproject.dto.MyScarpPostResponseDto
import com.kotlin.boardproject.dto.MyWrittenPostResponseDto
import com.kotlin.boardproject.dto.FindNormalPostByQueryRequestDto
import com.kotlin.boardproject.dto.post.*
import com.kotlin.boardproject.dto.post.normalpost.*
import org.springframework.data.domain.Pageable

interface PostService {
    // TODO: 게시물 생성
    // 3. 파일이 있다면 받는다. -> 아직 구현안함
    fun createNormalPost(
        userEmail: String,
        createNormalPostRequestDto: CreateNormalPostRequestDto,
    ): CreateNormalPostResponseDto

    // TODO: 게시물 하나 읽기
    fun findOneNormalPost(
        userEmail: String?,
        postId: Long,
    ): OneNormalPostResponseDto

    fun editNormalPost(
        userEmail: String,
        postId: Long,
        editNormalPostRequestDto: EditNormalPostRequestDto,
    ): EditNormalPostResponseDto

    fun deleteNormalPost(
        userEmail: String,
        postId: Long,
    ): DeleteNormalPostResponseDto

    fun blackPost(
        userEmail: String,
        postId: Long,
        blackPostRequestDto: BlackPostRequestDto,
    ): BlackPostResponseDto

    fun likePost(
        userEmail: String,
        postId: Long,
    ): LikePostResponseDto

    fun cancelLikePost(
        userEmail: String,
        postId: Long,
    ): CancelLikePostResponseDto

    fun scrapPost(
        userEmail: String,
        postId: Long,
    ): ScrapPostResponseDto

    fun cancelScrapPost(
        userEmail: String,
        postId: Long,
    ): CancelScrapPostResponseDto

    fun findNormalPostByQuery(
        userEmail: String?,
        pageable: Pageable,
        findNormalPostByQueryRequestDto: FindNormalPostByQueryRequestDto,
    ): FindNormalPostByQueryResponseDto

    fun findMyWrittenPost(
        userEmail: String,
        pageable: Pageable,
    ): MyWrittenPostResponseDto

    fun findMyScrapPost(
        userEmail: String,
        pageable: Pageable,
    ): MyScarpPostResponseDto
}
