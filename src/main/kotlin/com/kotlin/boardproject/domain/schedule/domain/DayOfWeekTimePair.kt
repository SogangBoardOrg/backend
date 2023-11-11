package com.kotlin.boardproject.domain.schedule.domain

import java.time.DayOfWeek
import java.time.LocalTime
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
class DayOfWeekTimePair(
    @Enumerated(EnumType.STRING)
    var dayOfWeek: DayOfWeek,

    var startTime: LocalTime,

    var endTime: LocalTime,
) {
    infix fun DayOfWeekTimePair.isOverlappedWith(other: DayOfWeekTimePair): Boolean {
        if (this.dayOfWeek != other.dayOfWeek) {
            return false
        }

        if (this.startTime.isBefore(other.startTime)) {
            return this.endTime.isAfter(other.startTime)
        }

        if (this.startTime.isAfter(other.startTime)) {
            return this.startTime.isBefore(other.endTime)
        }

        return true
    }
}
