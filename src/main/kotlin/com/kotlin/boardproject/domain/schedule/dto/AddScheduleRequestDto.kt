package com.kotlin.boardproject.domain.schedule.dto

import com.kotlin.boardproject.global.enums.AlphabetGrade

data class AddScheduleRequestDto(
    val title: String,
    val memo: String,
    val alphabetGrade: AlphabetGrade?,
    val credit: Float,
    val isMajor: Boolean,
    val professor: String,
    val location: String,
    val courseId: Long?,
    val dayOfWeekTimePairs: List<DayOfWeekTimePairDto>,
)
