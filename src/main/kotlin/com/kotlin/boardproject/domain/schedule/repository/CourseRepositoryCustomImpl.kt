package com.kotlin.boardproject.domain.schedule.repository

import com.kotlin.boardproject.domain.schedule.domain.Course
import com.kotlin.boardproject.domain.schedule.domain.QCourse.course
import com.kotlin.boardproject.global.enums.Seasons
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class CourseRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : CourseRepositoryCustom {
    override fun findByQuery(
        title: String?,
        major: String?,
        professor: String?,
        year: Int?,
        seasons: Seasons?,
        pageable: Pageable,
    ): Page<Course> {
        val dataIds = queryFactory
            .select(course.id)
            .from(course)
            .distinct()
            .where(
                titleContains(title),
                majorEq(major),
                professorEq(professor),
                yearEq(year),
                seasonsEq(seasons),
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(course.id.desc())
            .fetch()

        val totalCnt = queryFactory
            .select(course.id)
            .from(course)
            .distinct()
            .where(
                titleContains(title),
                majorEq(major),
                professorEq(professor),
                yearEq(year),
                seasonsEq(seasons),
            )
            .fetch()
            .size.toLong()

        val data = queryFactory
            .selectFrom(course)
            .distinct()
            .where(
                course.id.`in`(dataIds),
            )
            .leftJoin(course.dayOfWeekTimePairs).fetchJoin()
            .orderBy(course.id.desc())
            .fetch()

        return PageImpl(data.toList(), pageable, totalCnt)
    }

    private fun titleContains(title: String?) =
        if (title.isNullOrEmpty()) {
            null
        } else {
            course.title.contains(title)
        }

    private fun majorEq(major: String?) =
        if (major.isNullOrEmpty()) {
            null
        } else {
            course.majorDepartment.eq(major)
        }

    private fun professorEq(professor: String?) =
        if (professor.isNullOrEmpty()) {
            null
        } else {
            course.professor.eq(professor)
        }

    private fun yearEq(year: Int?) =
        if (year == null) {
            null
        } else {
            course.yearAndSeason.year.eq(year)
        }

    private fun seasonsEq(seasons: Seasons?) =
        if (seasons == null) {
            null
        } else {
            course.yearAndSeason.season.eq(seasons)
        }
}
