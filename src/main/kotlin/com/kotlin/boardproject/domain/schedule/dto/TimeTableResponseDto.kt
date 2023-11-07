package com.kotlin.boardproject.domain.schedule.dto

data class TimeTableResponseDto(
    val id: Long,
    val title: String,
    val scheduleList: List<ScheduleResponseDto>,
)
