package com.kotlin.boardproject.domain.schedule.domain

import com.kotlin.boardproject.global.domain.BaseEntity
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
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

    @Embedded
    val yearAndSeason: YearAndSeason,

    val credit: Float,

    val isMajor: Boolean,

    val professor: String,

    val locaton: String,
) : BaseEntity()
