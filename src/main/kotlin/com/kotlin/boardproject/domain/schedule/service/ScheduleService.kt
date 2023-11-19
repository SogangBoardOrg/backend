package com.kotlin.boardproject.domain.schedule.service

import com.kotlin.boardproject.domain.schedule.dto.AddScheduleRequestDto
import com.kotlin.boardproject.domain.schedule.dto.DeleteScheduleRequestDto
import com.kotlin.boardproject.domain.schedule.dto.DeleteScheduleResponseDto

interface ScheduleService {

    fun addSchedule(
        userEmail: String,
        timeTableId: Long,
        addScheduleRequestDto: AddScheduleRequestDto,
    ): Long

    fun deleteSchedule(
        userEmail: String,
        timeTableId: Long,
        deleteScheduleRequestDto: DeleteScheduleRequestDto,
    ): DeleteScheduleResponseDto
}
