package com.kotlin.boardproject.domain.schedule.dto

import com.kotlin.boardproject.domain.schedule.domain.Course
import org.springframework.data.domain.Page

data class CourseListByQueryDto(
    val contents: List<CourseResponseDto>,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val numberOfElements: Int,
    val size: Int,
) {
    companion object {
        fun fromCoursePageable(
            coursePageable: Page<Course>,
        ): CourseListByQueryDto {
            return CourseListByQueryDto(
                contents = coursePageable.map { CourseResponseDto.fromCourse(it) }.content,
                currentPage = coursePageable.number,
                totalPages = coursePageable.totalPages,
                totalElements = coursePageable.totalElements,
                numberOfElements = coursePageable.numberOfElements,
                size = coursePageable.size,
            )
        }
    }
}
