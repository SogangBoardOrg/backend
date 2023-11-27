package com.kotlin.boardproject.service

import com.kotlin.boardproject.domain.schedule.domain.TimeTable
import com.kotlin.boardproject.domain.schedule.domain.YearAndSeason
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.global.enums.Seasons

fun makeTimeTable(
    user: User,
): List<TimeTable> {
    return listOf(
        TimeTable(
            id = 1L,
            title = "시간표1",
            isPublic = true,
            isMain = true,
            user = user,
            yearAndSeason = YearAndSeason(
                year = 2021,
                season = Seasons.SPRING,
            ),
        ),
        TimeTable(
            id = 2L,
            title = "시간표2",
            isPublic = true,
            isMain = false,
            user = user,
            yearAndSeason = YearAndSeason(
                year = 2021,
                season = Seasons.SPRING,
            ),
        ),
        TimeTable(
            id = 3L,
            title = "시간표3",
            isPublic = false,
            isMain = false,
            user = user,
            yearAndSeason = YearAndSeason(
                year = 2023,
                season = Seasons.SPRING,
            ),
        ),
    )
}
