package com.kotlin.boardproject.domain.schedule.dto

import com.kotlin.boardproject.global.enums.Seasons

data class ScheduleResponseDto(
    val id: Long,
    val title: String,
    val memo: String,
    val year: Int,
    val season: Seasons,
)
