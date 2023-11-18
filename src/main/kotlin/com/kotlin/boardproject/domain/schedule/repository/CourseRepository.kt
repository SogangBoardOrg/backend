package com.kotlin.boardproject.domain.schedule.repository

import com.kotlin.boardproject.domain.schedule.domain.Course
import org.springframework.data.jpa.repository.JpaRepository

interface CourseRepository : JpaRepository<Course, Long>, CourseRepositoryCustom {

}
