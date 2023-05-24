package com.kotlin.boardproject.service

import com.kotlin.boardproject.dto.MyWrittenPostResponseDto
import com.kotlin.boardproject.dto.PostSearchDto
import com.kotlin.boardproject.dto.post.*
import com.kotlin.boardproject.dto.post.normalpost.*
import org.springframework.data.domain.Pageable

interface PostService {
    // TODO: 게시물 생성
    // 3. 파일이 있다면 받는다. -> 아직 구현안함
    fun createNormalPost(
        username: String,
        createNormalPostRequestDto: CreateNormalPostRequestDto,
    ): CreateNormalPostResponseDto

    // TODO: 게시물 하나 읽기
    fun findOneNormalPostById(
        username: String?,
        postId: Long,
    ): OneNormalPostResponseDto

    fun editNormalPost(
        username: String,
        postId: Long,
        editNormalPostRequestDto: EditNormalPostRequestDto,
    ): EditNormalPostResponseDto

    fun deleteNormalPost(
        username: String,
        postId: Long,
    ): DeleteNormalPostResponseDto

    fun blackPost(
        username: String,
        postId: Long,
        blackPostRequestDto: BlackPostRequestDto,
    ): BlackPostResponseDto

    fun likePost(
        username: String,
        postId: Long,
    ): LikePostResponseDto

    fun cancelLikePost(
        username: String,
        postId: Long,
    ): CancelLikePostResponseDto

    fun scrapPost(
        username: String,
        postId: Long,
    ): ScrapPostResponseDto

    fun cancelScrapPost(
        username: String,
        postId: Long,
    ): CancelScrapPostResponseDto

    fun findNormalPostByQuery(
        username: String?,
        pageable: Pageable,
        postSearchDto: PostSearchDto,
    ): QueryNormalPostSearchResponseDto

    fun findMyWrittenPost(
        username: String,
        pageable: Pageable,
    ): MyWrittenPostResponseDto
    // TODO: 핫한 게시물 보여주기 -> 별도 알고리즘

    // TODO: 어드민 전용 메뉴로 해당 게시물 정지하면 blackpost 상태 변경
}
