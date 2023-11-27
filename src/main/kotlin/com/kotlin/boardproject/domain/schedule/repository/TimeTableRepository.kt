package com.kotlin.boardproject.domain.schedule.repository

import com.kotlin.boardproject.domain.schedule.domain.TimeTable
import com.kotlin.boardproject.domain.schedule.domain.YearAndSeason
import com.kotlin.boardproject.domain.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TimeTableRepository : JpaRepository<TimeTable, Long> {

    @Query(
        """
        SELECT t
        FROM TimeTable t
        LEFT JOIN FETCH t.yearAndSeason
        WHERE t.user = :user
        """,
    )
    fun findByUserFetchYearAndSeason(
        user: User,
    ): List<TimeTable>

    @Query(
        """
        SELECT t
        FROM TimeTable t
        WHERE t.user = :user AND t.yearAndSeason = :yearAndSeason
        """,
    )
    fun findByUserAndYearAndSeason(
        user: User,
        yearAndSeason: YearAndSeason,
    ): List<TimeTable>

    @Query(
        """
        SELECT t
        FROM TimeTable t
        LEFT JOIN FETCH t.user
        WHERE t.id = :timeTableId
        """,
    )
    fun findByIdFetchUser(timeTableId: Long): TimeTable?

    @Query(
        """
        SELECT t
        FROM TimeTable t
        LEFT JOIN FETCH t.user
        LEFT JOIN FETCH t.schedules
        WHERE t.id = :timeTableId
        """,
    )
    fun findByIdFetchUserAndSchedule(timeTableId: Long): TimeTable?

    @Query(
        """
        SELECT t
        FROM TimeTable t
        LEFT JOIN FETCH t.user
        LEFT JOIN FETCH t.yearAndSeason
        WHERE t.id = :timeTableId
        """,
    )
    fun findByIdFetchUserAndYearAndSeason(timeTableId: Long): TimeTable?
}
