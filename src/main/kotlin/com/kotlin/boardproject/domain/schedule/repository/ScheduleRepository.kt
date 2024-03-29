package com.kotlin.boardproject.domain.schedule.repository

import com.kotlin.boardproject.domain.schedule.domain.Schedule
import com.kotlin.boardproject.domain.schedule.domain.TimeTable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface ScheduleRepository : JpaRepository<Schedule, Long> {

    @Query(
        """
        SELECT s
        FROM Schedule s
        LEFT JOIN FETCH s.dayOfWeekTimePairs
        WHERE s.timeTable = :timeTable
        """,
    )
    fun findByTimeTableFetchDayOfWeekTimePairs(timeTable: TimeTable): List<Schedule>

    @Query(
        """
        SELECT s
        FROM Schedule s
        LEFT JOIN FETCH s.timeTable
        WHERE s.id = :scheduleId
        """,
    )
    fun findByIdFetchTimetable(scheduleId: Long): Schedule?

    @Modifying
    @Transactional
    @Query(
        """
            DELETE
            FROM Schedule s
            WHERE s.id IN :scheduleIds
        """,
    )
    fun deleteByIdIn(scheduleIds: List<Long>)
}
