package com.kotlin.boardproject.service

import com.kotlin.boardproject.dto.CreatePostRequestDto
import com.kotlin.boardproject.dto.CreatePostResponseDto

interface PostService {
    // TODO: 게시물 생성
    // 1. 유저의 id를 받는다.
    // 2. 내용을 받는다.
    // 3. 파일이 있다면 받는다. -> 아직 구현안함
    fun createPost(username: String, createPostRequestDto: CreatePostRequestDto)


    // TODO: 게시물 하나 읽기

    // TODO: 게시물 전체 읽기 -> 게시판 들어가면

    // TODO: 게시물 검색해서 찾는 기능

    // TODO: 게시물 삭제하는 기능

    // TODO: 핫한 게시물 보여주기 -> 별도 알고리즘
}
