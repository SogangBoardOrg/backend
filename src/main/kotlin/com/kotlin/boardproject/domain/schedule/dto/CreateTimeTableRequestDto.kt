package com.kotlin.boardproject.domain.schedule.dto

import com.kotlin.boardproject.global.enums.Seasons

data class CreateTimeTableRequestDto(
    val year: Int,
    val season: Seasons,
    val title: String,
)
