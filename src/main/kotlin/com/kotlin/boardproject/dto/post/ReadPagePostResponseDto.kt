package com.kotlin.boardproject.dto.post

import com.kotlin.boardproject.dto.post.normalpost.OneNormalPostResponseDto

data class ReadPagePostResponseDto(
    val contents: List<OneNormalPostResponseDto>,
    val currentPage: Int,
    val totalPage: Int,
    val totalElements: Long,
)
