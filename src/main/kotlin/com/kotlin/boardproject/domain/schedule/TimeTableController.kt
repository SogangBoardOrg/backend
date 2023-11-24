package com.kotlin.boardproject.domain.schedule

import com.kotlin.boardproject.domain.schedule.dto.AddScheduleRequestDto
import com.kotlin.boardproject.domain.schedule.dto.AddScheduleResponseDto
import com.kotlin.boardproject.domain.schedule.dto.CreateTimeTableRequestDto
import com.kotlin.boardproject.domain.schedule.dto.CreateTimeTableResponseDto
import com.kotlin.boardproject.domain.schedule.dto.DeleteMyTimeTableResponseDto
import com.kotlin.boardproject.domain.schedule.dto.DeleteScheduleRequestDto
import com.kotlin.boardproject.domain.schedule.dto.DeleteScheduleResponseDto
import com.kotlin.boardproject.domain.schedule.dto.TimeTableResponseDto
import com.kotlin.boardproject.domain.schedule.service.ScheduleService
import com.kotlin.boardproject.domain.schedule.service.TimeTableService
import com.kotlin.boardproject.global.annotation.LoginUser
import com.kotlin.boardproject.global.dto.ApiResponse
import org.springframework.security.core.userdetails.User
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.Positive

@Validated
@RestController
@RequestMapping("/api/v1/timetable")
class TimeTableController(
    private val scheduleService: ScheduleService,
    private val timeTableService: TimeTableService,
) {
    @PostMapping("")
    fun createTimetable(
        @LoginUser loginUser: User,
        @RequestBody @Valid
        createTimeTableRequestDto: CreateTimeTableRequestDto,
    ): ApiResponse<CreateTimeTableResponseDto> {
        val date = timeTableService.createTimeTable(loginUser.username, createTimeTableRequestDto)

        return ApiResponse.success(date)
    }

    @GetMapping("/{timeTableId}")
    fun getTimeTableById(
        @LoginUser loginUser: User,
        @PathVariable @Positive
        timeTableId: Long,
    ): ApiResponse<TimeTableResponseDto> {
        val data = timeTableService.getTimeTableById(loginUser.username, timeTableId)

        return ApiResponse.success(data)
    }

    @DeleteMapping("/{timeTableId}")
    fun deleteTimetable(
        @LoginUser loginUser: User,
        @PathVariable @Positive
        timeTableId: Long,
    ): ApiResponse<DeleteMyTimeTableResponseDto> {
        val data = timeTableService.deleteMyTimeTableById(loginUser.username, timeTableId)

        return ApiResponse.success(data)
    }

    @PostMapping("/{timeTableId}/addSchedule")
    fun addSchdule(
        @LoginUser loginUser: User,
        @PathVariable @Positive
        timeTableId: Long,
        @RequestBody @Valid
        addScheduleRequestDto: AddScheduleRequestDto,
    ): ApiResponse<AddScheduleResponseDto> {
        val data = scheduleService.addSchedule(
            loginUser.username,
            timeTableId,
            addScheduleRequestDto,
        )

        return ApiResponse.success(data)
    }

    @DeleteMapping("/{timeTableId}/deleteSchedule")
    fun deleteSchdule(
        @LoginUser loginUser: User,
        @PathVariable @Positive
        timeTableId: Long,
        @RequestBody @Valid
        deleteScheduleRequestDto: DeleteScheduleRequestDto,
    ): ApiResponse<DeleteScheduleResponseDto> {
        val data = scheduleService.deleteSchedule(
            loginUser.username,
            timeTableId,
            deleteScheduleRequestDto,
        )

        return ApiResponse.success(data)
    }
}
