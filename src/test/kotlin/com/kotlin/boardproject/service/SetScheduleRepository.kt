package com.kotlin.boardproject.service

import com.kotlin.boardproject.domain.schedule.domain.TimeTable
import com.kotlin.boardproject.domain.schedule.repository.ScheduleRepository
import io.mockk.every

fun setScheduleTableRepository(
    timeTableOne: TimeTable,
    timeTableTwo: TimeTable,
    timeTableThree: TimeTable,
    scheduleRepository: ScheduleRepository,
) {
    every { scheduleRepository.findByTimeTableFetchDayOfWeekTimePairs(timeTableOne) } returns timeTableOne.schedules
    every { scheduleRepository.findByTimeTableFetchDayOfWeekTimePairs(timeTableTwo) } returns timeTableTwo.schedules
    every { scheduleRepository.findByTimeTableFetchDayOfWeekTimePairs(timeTableThree) } returns timeTableThree.schedules
}
