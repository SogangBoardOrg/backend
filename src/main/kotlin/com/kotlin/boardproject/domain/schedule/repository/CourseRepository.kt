package com.kotlin.boardproject.domain.schedule.repository

import com.kotlin.boardproject.domain.schedule.domain.Course
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CourseRepository : JpaRepository<Course, Long>, CourseRepositoryCustom {
    @Query(
        """
        SELECT c
        FROM Course c
        JOIN FETCH c.dayOfWeekTimePairs
        WHERE c.id = :courseId
        """,
    )
    fun finByIdFetchDayOfWeekTimePairs(courseId: Long): Course?
}
