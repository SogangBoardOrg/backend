package com.kotlin.boardproject.service

import com.kotlin.boardproject.dto.post.*
import com.kotlin.boardproject.dto.post.normalpost.*

interface PostService {
    // TODO: 게시물 생성
    // 3. 파일이 있다면 받는다. -> 아직 구현안함
    fun createNormalPost(
        username: String,
        createNormalPostRequestDto: CreateNormalPostRequestDto,
    ): CreateNormalPostResponseDto

    // TODO: 게시물 하나 읽기
    fun findOneNormalPostById(username: String?, postId: Long): OneNormalPostResponseDto

    // TODO: 게시물 페이지 읽기 -> 게시판 들어가면
    // fun readPagePost(): ReadPagePostResponseDto

    // TODO: 게시물 검색해서 찾는 기능 -> query dsl 사용
    // fun findNormalPost()

    fun editNormalPost(
        username: String,
        postId: Long,
        editNormalPostRequestDto: EditNormalPostRequestDto,
    ): EditNormalPostResponseDto

    fun deleteNormalPost(username: String, postId: Long): DeleteNormalPostResponseDto

    fun blackPost(username: String, postId: Long, blackPostRequestDto: BlackPostRequestDto): BlackPostResponseDto

    fun likePost(username: String, postId: Long): LikePostResponseDto

    fun cancelLikePost(username: String, postId: Long): CancelLikePostResponseDto

    fun scrapPost(username: String, postId: Long): ScrapPostResponseDto

    fun cancelScrapPost(username: String, postId: Long): CancelScrapPostResponseDto

    // TODO: 핫한 게시물 보여주기 -> 별도 알고리즘

    // TODO: 어드민 전용 메뉴로 해당 게시물 정지하면 blackpost 상태 변경
}
