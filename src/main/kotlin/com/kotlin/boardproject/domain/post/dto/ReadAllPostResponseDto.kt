package com.kotlin.boardproject.domain.post.dto

import com.kotlin.boardproject.domain.post.dto.normalpost.OneNormalPostResponseDto

data class ReadAllPostResponseDto(
    val postList: List<OneNormalPostResponseDto>,
)
