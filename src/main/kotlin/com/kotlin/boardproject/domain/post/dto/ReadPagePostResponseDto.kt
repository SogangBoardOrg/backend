package com.kotlin.boardproject.domain.post.dto

import com.kotlin.boardproject.domain.post.dto.normalpost.OneNormalPostResponseDto

data class ReadPagePostResponseDto(
    val contents: List<OneNormalPostResponseDto>,
    val currentPage: Int,
    val totalPage: Int,
    val totalElements: Long,
)
