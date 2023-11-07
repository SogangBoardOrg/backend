package com.kotlin.boardproject.domain.schedule.domain

import java.time.DayOfWeek
import java.time.LocalTime
import javax.persistence.Embeddable

@Embeddable
class TimeAndDayOfWeekPair(
    var dayOfWeek: DayOfWeek,

    var startTime: LocalTime,

    var endTime: LocalTime,
) {
    infix fun TimeAndDayOfWeekPair.isOverlappedWith(other: TimeAndDayOfWeekPair): Boolean {
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
