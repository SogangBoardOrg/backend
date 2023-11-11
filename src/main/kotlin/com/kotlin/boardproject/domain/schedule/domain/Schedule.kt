package com.kotlin.boardproject.domain.schedule.domain

import com.kotlin.boardproject.global.domain.BaseEntity
import com.kotlin.boardproject.global.enums.AlphabetGrade
import javax.persistence.Column
import javax.persistence.Embedded
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

    var memo: String,

    @Embedded
    var yearAndSeason: YearAndSeason,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_table_id")
    val timeTable: TimeTable,

    @Enumerated(EnumType.STRING)
    var alphabetGrade: AlphabetGrade,

    var credit: Float,

    var isMajor: Boolean,

    var professor: String,

    var location: String,
) : BaseEntity()
