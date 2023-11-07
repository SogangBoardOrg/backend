package com.kotlin.boardproject.domain.schedule.domain

import com.kotlin.boardproject.global.domain.BaseEntity
import javax.persistence.Column
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

) : BaseEntity()
