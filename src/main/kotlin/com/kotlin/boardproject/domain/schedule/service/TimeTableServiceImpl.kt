package com.kotlin.boardproject.domain.schedule.service

import com.kotlin.boardproject.domain.schedule.domain.TimeTable
import com.kotlin.boardproject.domain.schedule.domain.YearAndSeason
import com.kotlin.boardproject.domain.schedule.dto.CreateTimeTableRequestDto
import com.kotlin.boardproject.domain.schedule.dto.CreateTimeTableResponseDto
import com.kotlin.boardproject.domain.schedule.dto.DeleteMyTimeTableResponseDto
import com.kotlin.boardproject.domain.schedule.dto.MyTimeTableListResponseDto
import com.kotlin.boardproject.domain.schedule.dto.TimeTableResponseDto
import com.kotlin.boardproject.domain.schedule.repository.ScheduleRepository
import com.kotlin.boardproject.domain.schedule.repository.TimeTableRepository
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.global.enums.ErrorCode
import com.kotlin.boardproject.global.exception.ConditionConflictException
import com.kotlin.boardproject.global.exception.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TimeTableServiceImpl(
    private val timeTableRepository: TimeTableRepository,
    private val scheduleRepository: ScheduleRepository,
    private val userRepository: UserRepository,
) : TimeTableService {

    @Transactional
    override fun createTimeTable(
        userEmail: String,
        createTimeTableRequestDto: CreateTimeTableRequestDto,
    ): CreateTimeTableResponseDto {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("${userEmail}에 해당하는 유저가 없습니다.")

        val isNewTimeTableMain = timeTableRepository.findByUserAndYearAndSeason(
            user = user,
            yearAndSeason = YearAndSeason(
                year = createTimeTableRequestDto.year,
                season = createTimeTableRequestDto.season,
            ),
        ).isEmpty()

        val newTimeTable = timeTableRepository.save(
            TimeTable(
                user = user,
                yearAndSeason = YearAndSeason(
                    year = createTimeTableRequestDto.year,
                    season = createTimeTableRequestDto.season,
                ),
                title = createTimeTableRequestDto.title,
                isMain = isNewTimeTableMain,
                isPublic = createTimeTableRequestDto.isPublic,
            ),
        )

        return CreateTimeTableResponseDto(
            id = newTimeTable.id!!,
        )
    }

    @Transactional(readOnly = true)
    override fun getMyTimeTableList(
        userEmail: String,
    ): MyTimeTableListResponseDto {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("${userEmail}에 해당하는 유저가 없습니다.")

        val myTimeTables = timeTableRepository.findByUserFetchYearAndSeason(user)

        return MyTimeTableListResponseDto.fromTimeTableList(myTimeTables)
    }

    @Transactional(readOnly = true)
    override fun getTimeTableById(
        userEmail: String,
        timeTableId: Long,
    ): TimeTableResponseDto {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("${userEmail}에 해당하는 유저가 없습니다.")

        val timeTable = timeTableRepository.findByIdFetchUserAndSchedule(timeTableId)
            ?: throw EntityNotFoundException("${timeTableId}에 해당하는 시간표가 없습니다.")

        require(validateVisibility(user, timeTable)) {
            throw ConditionConflictException(ErrorCode.FORBIDDEN, "해당 시간표를 볼 수 있는 권한이 없습니다.")
        }

        val schedules = scheduleRepository.findByTimeTableFetchDayOfWeekTimePairs(timeTable)

        return TimeTableResponseDto.fromTimeTable(timeTable, schedules)
    }

    private fun validateVisibility(
        user: User,
        timeTable: TimeTable,
    ): Boolean {
        return timeTable.isOwner(user) || friendShow(user, timeTable)
    }

    private fun friendShow(
        user: User,
        timeTable: TimeTable,
    ): Boolean {
        // 2. 자신의 시간표는 아니지만 친구가 공개를 한 시간표 -> 볼 수 있음
        // 3. 자신의 시간표는 아니지만 친구가 공개를 하지 않은 시간표 -> 에러
        // 4. 친구가 아님
        return false
        // val friendList = user.friendList
        // val friendTimeTable = friendList.map { it.timeTableList }
        // return friendTimeTable.contains(timeTable)
    }

    @Transactional
    override fun deleteMyTimeTableById(
        userEmail: String,
        timeTableId: Long,
    ): DeleteMyTimeTableResponseDto {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("${userEmail}에 해당하는 유저가 없습니다.")

        val timeTable = timeTableRepository.findByIdFetchUserAndSchedule(timeTableId)
            ?: throw EntityNotFoundException("${timeTableId}에 해당하는 시간표가 없습니다.")

        require(timeTable.isOwner(user)) {
            throw ConditionConflictException(ErrorCode.FORBIDDEN, "해당 시간표를 삭제할 수 있는 권한이 없습니다.")
        }

        val scheduleIds = timeTable.schedules.map { it.id!! }

        scheduleRepository.deleteByIdIn(scheduleIds)
        timeTableRepository.delete(timeTable)

        return DeleteMyTimeTableResponseDto(
            id = timeTable.id!!,
        )
    }

    override fun changeTimeTableVisibility(
        userEmail: String,
        timeTableId: Long,
        isPublic: Boolean,
    ): Long {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("${userEmail}에 해당하는 유저가 없습니다.")

        val timeTable = timeTableRepository.findByIdFetchUser(timeTableId)
            ?: throw EntityNotFoundException("${timeTableId}에 해당하는 시간표가 없습니다.")

        require(timeTable.isOwner(user)) {
            throw ConditionConflictException(ErrorCode.FORBIDDEN, "해당 시간표를 수정할 수 있는 권한이 없습니다.")
        }

        timeTable.changeVisibility(isPublic)

        return timeTable.id!!
    }

    override fun makeTimeTableMain(
        userEmail: String,
        timeTableId: Long,
    ): Long {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("${userEmail}에 해당하는 유저가 없습니다.")

        val timeTable = timeTableRepository.findByIdFetchUserAndYearAndSeason(timeTableId)
            ?: throw EntityNotFoundException("해당하는 시간표가 없습니다.")

        require(timeTable.isOwner(user)) {
            throw ConditionConflictException(ErrorCode.FORBIDDEN, "해당 시간표를 수정할 수 있는 권한이 없습니다.")
        }

        timeTableRepository.findByUserAndYearAndSeason(user, timeTable.yearAndSeason).forEach {
            it.isMain = false
        }

        timeTable.isMain = true

        return timeTable.id!!
    }
}
