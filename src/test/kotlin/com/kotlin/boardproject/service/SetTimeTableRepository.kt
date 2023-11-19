package com.kotlin.boardproject.service

import com.kotlin.boardproject.domain.schedule.domain.TimeTable
import com.kotlin.boardproject.domain.schedule.domain.YearAndSeason
import com.kotlin.boardproject.domain.schedule.repository.TimeTableRepository
import com.kotlin.boardproject.global.enums.Seasons
import io.mockk.every

fun setTimeTableRepository(
    timeTableOne: TimeTable,
    timeTableTwo: TimeTable,
    timeTableThree: TimeTable,
    timeTableRepository: TimeTableRepository,
) {
    every {
        timeTableRepository.findByUserFetchYearAndSeason(timeTableOne.user)
    } returns listOf(timeTableOne, timeTableTwo, timeTableThree)

    every {
        timeTableRepository.findByUserAndYearAndSeason(
            user = timeTableOne.user,
            yearAndSeason = YearAndSeason(
                year = 2021,
                season = Seasons.SPRING,
            ),
        )
    } returns listOf(timeTableOne, timeTableTwo)

    every {
        timeTableRepository.findByUserAndYearAndSeason(
            user = timeTableThree.user,
            yearAndSeason = timeTableThree.yearAndSeason,
        )
    } returns listOf(timeTableThree)

    every {
        timeTableRepository.findByIdFetchUser(
            timeTableOne.id!!,
        )
    } returns timeTableOne

    every {
        timeTableRepository.findByIdFetchUser(
            timeTableTwo.id!!,
        )
    } returns timeTableTwo

    every {
        timeTableRepository.findByIdFetchUser(
            timeTableThree.id!!,
        )
    } returns timeTableThree

    every {
        timeTableRepository.findByIdFetchUserAndYearAndSeason(
            timeTableOne.id!!,
        )
    } returns timeTableOne

    every {
        timeTableRepository.findByIdFetchUserAndYearAndSeason(
            timeTableTwo.id!!,
        )
    } returns timeTableTwo

    every {
        timeTableRepository.findByIdFetchUserAndYearAndSeason(
            timeTableThree.id!!,
        )
    } returns timeTableThree

    every {
        timeTableRepository.findByIdFetchUserAndSchedule(
            timeTableOne.id!!,
        )
    } returns timeTableOne

    every {
        timeTableRepository.findByIdFetchUserAndSchedule(
            timeTableTwo.id!!,
        )
    } returns timeTableTwo

    every {
        timeTableRepository.findByIdFetchUserAndSchedule(
            timeTableThree.id!!,
        )
    } returns timeTableThree

    every { timeTableRepository.save(timeTableOne) } returns timeTableOne
    every { timeTableRepository.save(timeTableTwo) } returns timeTableTwo
    every { timeTableRepository.save(timeTableThree) } returns timeTableThree
}
