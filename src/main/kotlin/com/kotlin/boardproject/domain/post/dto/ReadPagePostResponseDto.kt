package com.kotlin.boardproject.domain.post.dto

import com.kotlin.boardproject.domain.post.dto.normalpost.OnePostResponseDto

data class ReadPagePostResponseDto(
    val contents: List<OnePostResponseDto>,
    val currentPage: Int,
    val totalPage: Int,
    val totalElements: Long,
)
