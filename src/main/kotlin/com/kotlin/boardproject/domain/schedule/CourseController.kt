package com.kotlin.boardproject.domain.schedule

import com.kotlin.boardproject.domain.schedule.dto.CourseListByQueryDto
import com.kotlin.boardproject.domain.schedule.dto.CourseResponseDto
import com.kotlin.boardproject.domain.schedule.service.CourseService
import com.kotlin.boardproject.global.dto.ApiResponse
import com.kotlin.boardproject.global.enums.Seasons
import org.springframework.data.domain.Pageable
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/api/v1/course")
class CourseController(
    private val courseService: CourseService,
) {

    @GetMapping("query")
    fun getCourseListByQuery(
        @RequestParam("title", required = false) title: String?,
        @RequestParam("major", required = false) major: String?,
        @RequestParam("year", required = false) year: Int?,
        @RequestParam("seasons", required = false) seasons: Seasons?,
        @RequestParam("professor", required = false) professor: String?,
        pageable: Pageable,
    ): ApiResponse<CourseListByQueryDto> {
        val courses = courseService.getCourseListByQuery(
            title = title,
            major = major,
            professor = professor,
            year = year,
            seasons = seasons,
            pageable = pageable,
        )
        TODO()
    }

    @GetMapping("/{courseId}")
    fun getCourseById(
        @RequestParam("course-id", required = true) courseId: Long,
    ): ApiResponse<CourseResponseDto> {
        TODO()
    }
}
