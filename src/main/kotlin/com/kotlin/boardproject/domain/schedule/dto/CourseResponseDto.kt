package com.kotlin.boardproject.domain.schedule.dto

data class CourseResponseDto(
    val id: Long,
    val title: String,
    val dayOfWeekTimePairs: List<DayOfWeekTimePairDto>,
    val credit: Float,
    val majorDepartment: String,
    val professor: String,
    val location: String,
    val year: Int,
    val season: String,
)
