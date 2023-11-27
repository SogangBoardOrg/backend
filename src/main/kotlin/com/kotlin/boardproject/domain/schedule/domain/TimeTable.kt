package com.kotlin.boardproject.domain.schedule.domain

import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.global.domain.BaseEntity
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
class TimeTable(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "time_table_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @Embedded
    var yearAndSeason: YearAndSeason,

    var title: String,

    @OneToMany(mappedBy = "timeTable")
    var schedules: MutableList<Schedule> = mutableListOf(),

    var isMain: Boolean,

    var isPublic: Boolean,
) : BaseEntity() {

    fun isOwner(user: User): Boolean {
        return this.user == user
    }

    fun changeVisibility(public: Boolean) {
        this.isPublic = public
    }
}
