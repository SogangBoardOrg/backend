package com.kotlin.boardproject.dto.post

import com.kotlin.boardproject.dto.post.normalpost.OneNormalPostResponseDto

data class ReadAllPostResponseDto(
    val postList: List<OneNormalPostResponseDto>,
)
