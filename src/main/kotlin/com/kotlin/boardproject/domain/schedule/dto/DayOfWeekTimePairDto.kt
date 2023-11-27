package com.kotlin.boardproject.domain.schedule.dto

import com.kotlin.boardproject.domain.schedule.domain.DayOfWeekTimePair
import com.kotlin.boardproject.global.annotation.StartTimeBeforeEndTime
import java.time.DayOfWeek
import java.time.LocalTime

@StartTimeBeforeEndTime
data class DayOfWeekTimePairDto(
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
    // TODO: startTime endTime validation 하기
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
