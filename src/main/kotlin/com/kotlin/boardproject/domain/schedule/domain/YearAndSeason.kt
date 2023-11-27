package com.kotlin.boardproject.domain.schedule.domain

import com.kotlin.boardproject.global.enums.Seasons
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
class YearAndSeason(
    val year: Int,
    @Enumerated(EnumType.STRING)
    val season: Seasons,
) {
    override fun toString(): String {
        return "${year}년 ${season}학기"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as YearAndSeason

        if (season != other.season) return false
        if (year != other.year) return false

        return true
    }

    override fun hashCode(): Int {
        var result = season.hashCode()
        result = 31 * result + year
        return result
    }
}
