package com.kotlin.boardproject.domain.schedule.service

import com.kotlin.boardproject.domain.schedule.dto.AddScheduleRequestDto

interface ScheduleService {

    fun addSchedule(
        userEmail: String,
        timeTableId: Long,
        addScheduleRequestDto: AddScheduleRequestDto,
    ): Long

    fun deleteSchedule(
        userEmail: String,
        timeTableId: Long,
        scheduleId: Long,
    )
}
