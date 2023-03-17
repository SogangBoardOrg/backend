package com.kotlin.boardproject.service

import com.kotlin.boardproject.dto.CreatePostRequestDto
import com.kotlin.boardproject.dto.ReadOnePostResponseDto
import com.kotlin.boardproject.dto.ReadPagePostResponseDto

interface PostService {
    // TODO: 게시물 생성
    // 1. 유저의 id를 받는다.
    // 2. 내용을 받는다.
    // 3. 파일이 있다면 받는다. -> 아직 구현안함
    fun createPost(username: String, createPostRequestDto: CreatePostRequestDto)

    // TODO: 게시물 하나 읽기
    fun readOnePost(postId: Long): ReadOnePostResponseDto

    // TODO: 게시물 페이지 읽기 -> 게시판 들어가면
    fun readPagePost(): ReadPagePostResponseDto

    // TODO: 게시물 검색해서 찾는 기능 -> query dsl 사용
    // fun findPost()


    // TODO: 게시물 삭제하는 기능
    fun deletePost()

    // TODO: 핫한 게시물 보여주기 -> 별도 알고리즘
}
