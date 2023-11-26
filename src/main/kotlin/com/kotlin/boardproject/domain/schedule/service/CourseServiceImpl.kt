package com.kotlin.boardproject.domain.schedule.service

import com.kotlin.boardproject.domain.schedule.dto.CourseListByQueryDto
import com.kotlin.boardproject.domain.schedule.dto.CourseResponseDto
import com.kotlin.boardproject.domain.schedule.repository.CourseRepository
import com.kotlin.boardproject.global.enums.Seasons
import com.kotlin.boardproject.global.exception.EntityNotFoundException
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
        season: Seasons?,
        pageable: Pageable,
    ): CourseListByQueryDto {
        val courses = courseRepository.findByQuery(
            title,
            major,
            professor,
            year,
            season,
            pageable,
        )

        return CourseListByQueryDto.fromCoursePageable(courses)
    }

    override fun getCourseById(
        courseId: Long,
    ): CourseResponseDto {
        val course = courseRepository.finByIdFetchDayOfWeekTimePairs(courseId)
            ?: throw EntityNotFoundException("$courseId 번의 강의를 찾을 수 없습니다.")

        return CourseResponseDto.fromCourse(course)
    }
}
