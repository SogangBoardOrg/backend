package com.kotlin.boardproject.service

import com.kotlin.boardproject.domain.schedule.domain.TimeTable
import com.kotlin.boardproject.domain.schedule.domain.YearAndSeason
import com.kotlin.boardproject.domain.schedule.dto.CreateTimeTableRequestDto
import com.kotlin.boardproject.domain.schedule.repository.ScheduleRepository
import com.kotlin.boardproject.domain.schedule.repository.TimeTableRepository
import com.kotlin.boardproject.domain.schedule.service.TimeTableServiceImpl
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.global.enums.Seasons
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk

class TimeTableServiceImplTest : BehaviorSpec(
    {
        isolationMode = IsolationMode.InstancePerTest

        val (userOne, userTwo, userThree) = makeUser()
        val (timeTableOne, timeTableTwo, timeTableThree) = makeTimeTable(userOne)

        val userRepository: UserRepository = mockk()
        val timeTableRepository: TimeTableRepository = mockk()
        val scheduleRepository: ScheduleRepository = mockk()

        val timeTableService = TimeTableServiceImpl(
            userRepository = userRepository,
            timeTableRepository = timeTableRepository,
            scheduleRepository = scheduleRepository,
        )

        setUserRepository(
            userOne,
            userTwo,
            userThree,
            userRepository,
        )

        setTimeTableRepository(
            timeTableOne,
            timeTableTwo,
            timeTableThree,
            timeTableRepository,
        )

        // 시간표를 만드는 요청
        Given("시간표를 만드는 요청") {
            When("시간표 정상 생성") {
                val createTimeTableRequestDto = CreateTimeTableRequestDto(
                    title = "시간표1",
                    isPublic = true,
                    year = 2021,
                    season = Seasons.SPRING,
                )

                val data = timeTableService.createTimeTable(
                    userEmail = userOne.email,
                    createTimeTableRequestDto = createTimeTableRequestDto,
                )

                Then("시간표를 만든다") {
                    // 시간표를 만드는 요청
                    // 시간표를 만든다

                }
            }
        }

        // x 시간표를 가져오는 요청
        Given("시간표를 id로 조회하는 요청") {
            When("시간표를 id로 조회하는 요청") {
                Then("시간표를 조회한다") {
                    // 시간표를 id로 조회하는 요청
                    // 시간표를 조회한다
                }
            }
        }

        // 시간표를 삭제하는 요청

    },
)
