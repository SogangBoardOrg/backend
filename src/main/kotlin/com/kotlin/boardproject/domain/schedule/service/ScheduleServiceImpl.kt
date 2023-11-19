package com.kotlin.boardproject.domain.schedule.service

import com.kotlin.boardproject.domain.schedule.domain.DayOfWeekTimePair
import com.kotlin.boardproject.domain.schedule.domain.Schedule
import com.kotlin.boardproject.domain.schedule.dto.AddScheduleRequestDto
import com.kotlin.boardproject.domain.schedule.dto.DeleteScheduleRequestDto
import com.kotlin.boardproject.domain.schedule.dto.DeleteScheduleResponseDto
import com.kotlin.boardproject.domain.schedule.repository.CourseRepository
import com.kotlin.boardproject.domain.schedule.repository.ScheduleRepository
import com.kotlin.boardproject.domain.schedule.repository.TimeTableRepository
import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.global.enums.ErrorCode
import com.kotlin.boardproject.global.exception.ConditionConflictException
import com.kotlin.boardproject.global.exception.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ScheduleServiceImpl(
    private val scheduleRepository: ScheduleRepository,
    private val courseRepository: CourseRepository,
    private val timeTableRepository: TimeTableRepository,
    private val userRepository: UserRepository,
) : ScheduleService {

    @Transactional
    override fun addSchedule(
        userEmail: String,
        timeTableId: Long,
        addScheduleRequestDto: AddScheduleRequestDto,
    ): Long {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("존재하지 않는 유저입니다.")

        val course = addScheduleRequestDto.courseId?.let {
            courseRepository.findByIdOrNull(it)
                ?: throw EntityNotFoundException("존재하지 않는 과목입니다.")
        }

        val timeTable = timeTableRepository.findByIdFetchUser(timeTableId)
            ?: throw EntityNotFoundException("존재하지 않는 시간표입니다.")

        require(timeTable.isOwner(user)) {
            throw ConditionConflictException(ErrorCode.FORBIDDEN, "시간표의 주인이 아닙니다.")
        }

        val newDayOfWeekTimePairs = addScheduleRequestDto.dayOfWeekTimePairs.map {
            DayOfWeekTimePair(
                dayOfWeek = it.dayOfWeek,
                startTime = it.startTime,
                endTime = it.endTime,
            )
        }

        require(newDayOfWeekTimePairs.isNotEmpty()) {
            throw ConditionConflictException(ErrorCode.CONDITION_NOT_FULFILLED, "시간표에 추가할 시간이 없습니다.")
        }

        require(
            newSchedulesValid(newDayOfWeekTimePairs),
        ) {
            throw ConditionConflictException(ErrorCode.CONDITION_NOT_FULFILLED, "시간이 올바르지 않습니다.")
        }

        require(
            areSchedulesValid(
                newDayOfWeekTimePairs,
                timeTable.schedules.flatMap { it.dayOfWeekTimePairs },
            ),
        ) {
            throw ConditionConflictException(ErrorCode.CONDITION_NOT_FULFILLED, "시간표에 이미 존재하는 시간입니다.")
        }

        return scheduleRepository.save(
            Schedule(
                title = addScheduleRequestDto.title,
                memo = addScheduleRequestDto.memo,
                dayOfWeekTimePairs = newDayOfWeekTimePairs.toMutableList(),
                timeTable = timeTable,
                alphabetGrade = addScheduleRequestDto.alphabetGrade,
                credit = addScheduleRequestDto.credit,
                isMajor = addScheduleRequestDto.isMajor,
                professor = addScheduleRequestDto.professor,
                location = addScheduleRequestDto.location,
                course = course,
                majorDepartment = addScheduleRequestDto.majorDepartment,
            ),
        ).id!!
    }

    private fun newSchedulesValid(
        newDayOfWeekTimePairs: List<DayOfWeekTimePair>,
    ): Boolean {
        return newDayOfWeekTimePairs.all { newPair ->
            newDayOfWeekTimePairs.none { it != newPair && it overlap newPair }
        }
    }

    private fun areSchedulesValid(
        oldDayOfWeekTimePairs: List<DayOfWeekTimePair>,
        newDayOfWeekTimePairs: List<DayOfWeekTimePair>,
    ): Boolean {
        return oldDayOfWeekTimePairs.all { oldPair ->
            newDayOfWeekTimePairs.none { newPair -> oldPair overlap newPair }
        }
    }

    @Transactional
    override fun deleteSchedule(
        userEmail: String,
        timeTableId: Long,
        deleteScheduleRequestDto: DeleteScheduleRequestDto,
    ): DeleteScheduleResponseDto {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("$userEmail 에 해당하는 유저가 존재하지 않습니다.")

        val timeTable = timeTableRepository.findByIdFetchUser(timeTableId)
            ?: throw EntityNotFoundException("존재하지 않는 시간표입니다.")

        require(timeTable.isOwner(user)) {
            throw ConditionConflictException(ErrorCode.FORBIDDEN, "시간표의 주인이 아닙니다.")
        }

        val schedule = scheduleRepository.findByIdFetchTimetable(deleteScheduleRequestDto.scheduleId)
            ?: throw EntityNotFoundException("존재하지 않는 스케쥴입니다.")

        require(schedule.timeTable == timeTable) {
            throw ConditionConflictException(ErrorCode.CONDITION_NOT_FULFILLED, "시간표에 존재하지 않는 시간입니다.")
        }

        scheduleRepository.delete(schedule)

        return DeleteScheduleResponseDto(
            scheduleId = schedule.id!!,
        )
    }
}
