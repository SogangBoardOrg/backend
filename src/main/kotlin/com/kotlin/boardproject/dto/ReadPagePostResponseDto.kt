package com.kotlin.boardproject.dto

data class ReadPagePostResponseDto(
    val contents: List<ReadOnePostResponseDto>,
    val currentPage: Int,
    val totalPage: Int,
    val totalElements: Long
)
