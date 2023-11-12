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
    infix fun overlap(other: DayOfWeekTimePair): Boolean {
        if (this.dayOfWeek != other.dayOfWeek) {
            return false
        }
        if (this.endTime.isBefore(other.startTime) || this.startTime.isAfter(other.endTime)) {
            return false
        }
        return true
    }
}
