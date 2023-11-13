package com.kotlin.boardproject.domain.schedule.domain

import com.kotlin.boardproject.global.domain.BaseEntity
import com.kotlin.boardproject.global.enums.AlphabetGrade
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class Schedule(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    var id: Long? = null,

    var title: String,

    @ElementCollection(fetch = FetchType.LAZY)
    val dayOfWeekTimePairs: MutableList<DayOfWeekTimePair> = mutableListOf(),

    var credit: Float,

    var isMajor: Boolean,

    var professor: String,

    var location: String,

    var memo: String,

    @Enumerated(EnumType.STRING)
    var alphabetGrade: AlphabetGrade?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_table_id")
    val timeTable: TimeTable,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    val course: Course? = null,
) : BaseEntity()
