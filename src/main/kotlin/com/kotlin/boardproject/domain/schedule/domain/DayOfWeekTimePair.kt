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
        return this.dayOfWeek == other.dayOfWeek &&
            this.endTime.isAfter(other.startTime) &&
            this.startTime.isBefore(other.endTime)
    }
}
