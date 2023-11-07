package com.kotlin.boardproject.domain.schedule.dto

data class MyTimeTableListResponseDto(
    val id: Long,
    val year: Int,
    val seasons: String,
    val name: String,
)
