package com.kotlin.boardproject.dto

import com.kotlin.boardproject.dto.post.normalpost.OneNormalPostResponseDto

data class ReadAllPostResponseDto(
    val postList: List<OneNormalPostResponseDto>
)
