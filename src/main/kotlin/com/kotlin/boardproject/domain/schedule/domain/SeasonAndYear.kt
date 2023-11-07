package com.kotlin.boardproject.domain.schedule.domain

import com.kotlin.boardproject.global.enums.Seasons
import javax.persistence.Embeddable

@Embeddable
class SeasonAndYear(
    val season: Seasons,
    val year: Int,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SeasonAndYear

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
