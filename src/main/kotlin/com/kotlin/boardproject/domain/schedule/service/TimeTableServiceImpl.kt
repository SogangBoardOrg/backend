package com.kotlin.boardproject.domain.schedule.service

import com.kotlin.boardproject.domain.schedule.domain.SeasonAndYear
import com.kotlin.boardproject.domain.schedule.domain.TimeTable
import com.kotlin.boardproject.domain.schedule.dto.CreateTimeTableRequestDto
import com.kotlin.boardproject.domain.schedule.dto.CreateTimeTableResponseDto
import com.kotlin.boardproject.domain.schedule.dto.MyTimeTableListResponseDto
import com.kotlin.boardproject.domain.schedule.dto.TimeTableResponseDto
import com.kotlin.boardproject.domain.schedule.repository.ScheduleRepository
import com.kotlin.boardproject.domain.schedule.repository.TimeTableRepository
import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.global.exception.EntityNotFoundException

class TimeTableServiceImpl(
    private val timeTableRepository: TimeTableRepository,
    private val scheduleRepository: ScheduleRepository,
    private val userRepository: UserRepository,
) : TimeTableService {
    override fun createTimeTable(
        userEmail: String,
        createTimeTableRequestDto: CreateTimeTableRequestDto,
    ): CreateTimeTableResponseDto {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("${userEmail}에 해당하는 유저가 없습니다.")

        val isNewTimeTableMain = timeTableRepository.findByUserAndSeasonAndYear(
            user = user,
            seasonAndYear = SeasonAndYear(
                year = createTimeTableRequestDto.year,
                season = createTimeTableRequestDto.season,
            ),
        ).isEmpty()

        val newTimeTable = timeTableRepository.save(
            TimeTable(
                user = user,
                seasonAndYear = SeasonAndYear(
                    year = createTimeTableRequestDto.year,
                    season = createTimeTableRequestDto.season,
                ),
                title = createTimeTableRequestDto.title,
                isMain = isNewTimeTableMain,
            ),
        )

        return CreateTimeTableResponseDto(
            id = newTimeTable.id!!,
        )
    }

    override fun getMyTimeTableList(
        userEmail: String,
    ): List<MyTimeTableListResponseDto> {
        TODO("Not yet implemented")
    }

    override fun getTimeTableById(
        userEmail: String,
        timeTableId: Long,
    ): TimeTableResponseDto {
        TODO("Not yet implemented")
    }

    override fun deleteMyTimeTable(
        userEmail: String,
        timeTableId: Long,
    ): Boolean {
        TODO("Not yet implemented")
    }
}
