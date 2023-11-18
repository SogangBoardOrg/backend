package com.kotlin.boardproject.domain.schedule.dto

data class CourseListByQueryDto(
    val contents: List<CourseResponseDto>,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val numberOfElements: Int,
    val size: Int,
)
