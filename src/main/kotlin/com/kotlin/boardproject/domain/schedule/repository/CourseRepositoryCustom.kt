package com.kotlin.boardproject.domain.schedule.repository

import com.kotlin.boardproject.domain.schedule.domain.Course
import com.kotlin.boardproject.global.enums.Seasons
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CourseRepositoryCustom {
    fun findByQuery(
        title: String?,
        major: String?,
        professor: String?,
        year: Int?,
        seasons: Seasons?,
        courseCode: String?,
        pageable: Pageable,
    ): Page<Course>
}
