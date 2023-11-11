package com.kotlin.boardproject.domain.schedule.dto

import com.kotlin.boardproject.domain.schedule.domain.Schedule
import com.kotlin.boardproject.domain.schedule.domain.TimeTable
import com.kotlin.boardproject.global.enums.Seasons

data class TimeTableResponseDto(
    val id: Long,
    val title: String,
    val isMain: Boolean,
    val isPublic: Boolean,
    val year: Int,
    val season: Seasons,
    val scheduleList: List<ScheduleResponseDto>,
){
    companion object{
        fun fromTimeTable(
            timeTable: TimeTable,
            schedules: List<Schedule>,
        ): TimeTableResponseDto{
            return TimeTableResponseDto(
                id = timeTable.id!!,
                title = timeTable.title,
                isMain = timeTable.isMain,
                isPublic = timeTable.isPublic,
                year = timeTable.yearAndSeason.year,
                season = timeTable.yearAndSeason.season,
                scheduleList = schedules.map {
                    ScheduleResponseDto(
                        id = it.id!!,
                        title = it.title,
                        memo = it.memo,
                        year = it.yearAndSeason.year,
                        season = it.yearAndSeason.season,
                    )
                },
            )
        }
    }
}
