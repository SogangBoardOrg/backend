package com.kotlin.boardproject.domain.schedule.repository

import com.kotlin.boardproject.domain.schedule.domain.Schedule
import com.kotlin.boardproject.domain.schedule.domain.TimeTable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ScheduleRepository : JpaRepository<Schedule, Long> {

    @Query(
        """
        SELECT s
        FROM Schedule s
        LEFT JOIN FETCH s.yearAndSeason
        WHERE s.timeTable = :timeTable
        """,
    )
    fun findByTimeTableFetchYearAndSeasons(timeTable: TimeTable): List<Schedule>
}
