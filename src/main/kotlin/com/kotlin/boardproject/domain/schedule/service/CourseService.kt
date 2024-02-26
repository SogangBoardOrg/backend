package com.kotlin.boardproject.domain.schedule.service

import com.kotlin.boardproject.domain.schedule.dto.CourseListByQueryDto
import com.kotlin.boardproject.domain.schedule.dto.CourseResponseDto
import com.kotlin.boardproject.global.enums.Seasons
import org.springframework.data.domain.Pageable

interface CourseService {

    fun getCourseListByQuery(
        title: String?,
        major: String?,
        professor: String?,
        year: Int?,
        season: Seasons?,
        courseCode: String?,
        pageable: Pageable,
    ): CourseListByQueryDto

    fun getCourseById(
        courseId: Long,
    ): CourseResponseDto
}
