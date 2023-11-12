package com.kotlin.boardproject.domain.schedule.dto

data class ScheduleResponseDto(
    val id: Long,
    val title: String,
    val memo: String,
    val dayOfWeekTimePairs: List<DayOfWeekTimePairDto>,
)
