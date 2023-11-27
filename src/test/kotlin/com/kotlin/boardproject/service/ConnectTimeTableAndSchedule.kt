package com.kotlin.boardproject.service

import com.kotlin.boardproject.domain.schedule.domain.DayOfWeekTimePair
import com.kotlin.boardproject.domain.schedule.domain.Schedule
import com.kotlin.boardproject.domain.schedule.domain.TimeTable
import java.time.DayOfWeek
import java.time.LocalTime

fun connectTimeTableAndSchedule(timeTable: TimeTable) {
    val schedule_1 = Schedule(
        id = timeTable.id!! * 2,
        title = "schedule_1",
        memo = "schedule_1",
        timeTable = timeTable,
        dayOfWeekTimePairs = listOf(
            DayOfWeekTimePair(
                dayOfWeek = DayOfWeek.MONDAY,
                startTime = LocalTime.of(1, 0),
                endTime = LocalTime.of(3, 0),
            ),
            DayOfWeekTimePair(
                dayOfWeek = DayOfWeek.TUESDAY,
                startTime = LocalTime.of(1, 0),
                endTime = LocalTime.of(3, 0),
            ),
        ).toMutableList(),
        alphabetGrade = null,
        credit = 2.5F,
        isMajor = true,
        professor = "professor_1",
        location = "location_1",
        majorDepartment = "majorDepartment_1",
    )

    val schedule_2 = Schedule(
        id = timeTable.id!! * 2 + 1,
        title = "schedule_2",
        memo = "schedule_2",
        timeTable = timeTable,
        dayOfWeekTimePairs = listOf(
            DayOfWeekTimePair(
                dayOfWeek = DayOfWeek.THURSDAY,
                startTime = LocalTime.of(21, 0),
                endTime = LocalTime.of(23, 0),
            ),
            DayOfWeekTimePair(
                dayOfWeek = DayOfWeek.FRIDAY,
                startTime = LocalTime.of(2, 0),
                endTime = LocalTime.of(12, 0),
            ),
        ).toMutableList(),
        alphabetGrade = null,
        credit = 2.5F,
        isMajor = true,
        professor = "professor_1",
        location = "location_1",
        majorDepartment = "majorDepartment_1",
    )

    timeTable.schedules.add(schedule_1)
    timeTable.schedules.add(schedule_2)
}
