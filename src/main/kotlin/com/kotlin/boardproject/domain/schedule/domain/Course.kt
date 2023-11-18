package com.kotlin.boardproject.domain.schedule.domain

import com.kotlin.boardproject.global.domain.BaseEntity
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Course(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    var id: Long? = null,

    val title: String,

    @ElementCollection(fetch = FetchType.LAZY)
    val dayOfWeekTimePairs: MutableList<DayOfWeekTimePair> = mutableListOf(),

    val credit: Float,

    val majorDepartment: String,

    val professor: String,

    val locaton: String,

    @Embedded
    val yearAndSeason: YearAndSeason,

) : BaseEntity()
