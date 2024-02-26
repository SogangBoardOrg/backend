package com.kotlin.boardproject.domain.schedule.dto

import com.kotlin.boardproject.domain.schedule.domain.Course
import com.kotlin.boardproject.global.enums.Seasons

data class CourseResponseDto(
    val id: Long,
    val title: String,
    val courseCode: String,
    val dayOfWeekTimePairs: List<DayOfWeekTimePairDto>,
    val credit: Float,
    val majorDepartment: String,
    val professor: String,
    val location: String,
    val year: Int,
    val season: Seasons,
) {
    companion object {
        fun fromCourse(
            course: Course,
        ): CourseResponseDto {
            return CourseResponseDto(
                id = course.id!!,
                title = course.title,
                courseCode = course.courseCode,
                dayOfWeekTimePairs = course.dayOfWeekTimePairs.map { dayOfWeekTimePair ->
                    DayOfWeekTimePairDto(
                        dayOfWeek = dayOfWeekTimePair.dayOfWeek,
                        startTime = dayOfWeekTimePair.startTime,
                        endTime = dayOfWeekTimePair.endTime,
                    )
                },
                credit = course.credit,
                majorDepartment = course.majorDepartment,
                professor = course.professor,
                location = course.locaton,
                year = course.yearAndSeason.year,
                season = course.yearAndSeason.season,
            )
        }
    }
}
