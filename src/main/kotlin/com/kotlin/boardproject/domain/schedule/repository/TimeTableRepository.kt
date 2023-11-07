package com.kotlin.boardproject.domain.schedule.repository

import com.kotlin.boardproject.domain.schedule.domain.SeasonAndYear
import com.kotlin.boardproject.domain.schedule.domain.TimeTable
import com.kotlin.boardproject.domain.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TimeTableRepository : JpaRepository<TimeTable, Long> {

    @Query(
        """
        SELECT t
        FROM TimeTable t
        WHERE t.user = :user AND t.seasonAndYear = :seasonAndYear
        """,
    )
    fun findByUserAndSeasonAndYear(
        user: User,
        seasonAndYear: SeasonAndYear,
    ): List<TimeTable>
}
