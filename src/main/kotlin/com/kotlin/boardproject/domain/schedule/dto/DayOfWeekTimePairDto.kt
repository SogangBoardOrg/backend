package com.kotlin.boardproject.domain.schedule.dto

import com.kotlin.boardproject.domain.schedule.domain.DayOfWeekTimePair
import java.time.DayOfWeek
import java.time.LocalTime

data class DayOfWeekTimePairDto(
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
) {
    companion object {
        fun fromDayOfWeekTimePair(
            dayOfWeekTimePair: DayOfWeekTimePair,
        ): DayOfWeekTimePairDto {
            return DayOfWeekTimePairDto(
                dayOfWeek = dayOfWeekTimePair.dayOfWeek,
                startTime = dayOfWeekTimePair.startTime,
                endTime = dayOfWeekTimePair.endTime,
            )
        }
    }
}
