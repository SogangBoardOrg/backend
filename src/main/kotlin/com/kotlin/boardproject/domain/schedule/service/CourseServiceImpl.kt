package com.kotlin.boardproject.domain.schedule.service

import com.kotlin.boardproject.domain.schedule.dto.CourseListByQueryDto
import com.kotlin.boardproject.domain.schedule.dto.CourseResponseDto
import com.kotlin.boardproject.domain.schedule.repository.CourseRepository
import com.kotlin.boardproject.global.enums.Seasons
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class CourseServiceImpl(
    private val courseRepository: CourseRepository,
) : CourseService {
    override fun getCourseListByQuery(
        title: String?,
        major: String?,
        professor: String?,
        year: Int?,
        seasons: Seasons?,
        pageable: Pageable,
    ): CourseListByQueryDto {
        val courses = courseRepository.findByQuery(
            title,
            major,
            professor,
            year,
            seasons,
            pageable)
        TODO()
    }

    override fun getCourseById(
        courseId: Long,
    ): CourseResponseDto {
        TODO()
    }
}
