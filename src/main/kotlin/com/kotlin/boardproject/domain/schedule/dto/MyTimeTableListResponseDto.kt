package com.kotlin.boardproject.domain.schedule.dto

import com.kotlin.boardproject.domain.schedule.domain.TimeTable
import com.kotlin.boardproject.domain.schedule.domain.YearAndSeason

data class MyTimeTableListResponseDto(
    val timeTableMap: Map<YearAndSeason, List<TimeTablePreviewResponseDto>>,
) {
    data class TimeTablePreviewResponseDto(
        val id: Long,
        val title: String,
        val isMain: Boolean,
        val isPublic: Boolean,
    )

    companion object {
        fun fromTimeTableList(
            timeTables: List<TimeTable>,
        ): MyTimeTableListResponseDto {
            return MyTimeTableListResponseDto(
                timeTableMap = timeTables.groupBy(
                    keySelector = { it.yearAndSeason },
                    valueTransform = {
                        TimeTablePreviewResponseDto(
                            id = it.id!!,
                            title = it.title,
                            isMain = it.isMain,
                            isPublic = it.isPublic,
                        )
                    },
                ),
            )
        }
    }
}
