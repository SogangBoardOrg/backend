package com.kotlin.boardproject.domain.schedule.dto

import com.kotlin.boardproject.global.enums.Seasons
import javax.validation.constraints.Positive

data class CreateTimeTableRequestDto(
    @field:Positive
    val year: Int,
    val season: Seasons,
    val title: String,
    val isPublic: Boolean,
)
