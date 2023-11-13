package com.kotlin.boardproject.service

import com.kotlin.boardproject.domain.schedule.domain.TimeTable
import com.kotlin.boardproject.domain.schedule.domain.YearAndSeason
import com.kotlin.boardproject.domain.schedule.dto.CreateTimeTableRequestDto
import com.kotlin.boardproject.domain.schedule.repository.ScheduleRepository
import com.kotlin.boardproject.domain.schedule.repository.TimeTableRepository
import com.kotlin.boardproject.domain.schedule.service.TimeTableServiceImpl
import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.global.enums.Seasons
import com.kotlin.boardproject.global.exception.ConditionConflictException
import com.kotlin.boardproject.global.exception.EntityNotFoundException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
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

        setScheduleTableRepository(
            timeTableOne,
            timeTableTwo,
            timeTableThree,
            scheduleRepository,
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

                every { timeTableRepository.save(any()) } returns TimeTable(
                    id = 4L,
                    user = userOne,
                    yearAndSeason = YearAndSeason(
                        year = createTimeTableRequestDto.year,
                        season = createTimeTableRequestDto.season,
                    ),
                    title = createTimeTableRequestDto.title,
                    isMain = false,
                    isPublic = createTimeTableRequestDto.isPublic,
                )

                val data = timeTableService.createTimeTable(
                    userEmail = userOne.email,
                    createTimeTableRequestDto = createTimeTableRequestDto,
                )

                Then("시간표를 만든다") {
                    // 시간표를 만드는 요청
                    // 시간표를 만든다
                    data.id shouldBe 4L
                }
            }

            When("없는 유저가 시간표 생성 시도") {
                val createTimeTableRequestDto = CreateTimeTableRequestDto(
                    title = "시간표1",
                    isPublic = true,
                    year = 2021,
                    season = Seasons.SPRING,
                )

                val error = shouldThrow<EntityNotFoundException> {
                    timeTableService.createTimeTable(
                        userEmail = nonExistUserEmail,
                        createTimeTableRequestDto = createTimeTableRequestDto,
                    )
                }

                Then("에러") {
                    error.log shouldBe "${nonExistUserEmail}에 해당하는 유저가 없습니다."
                }
            }
        }

        // x 시간표를 가져오는 요청
        Given("시간표를 id로 조회하는 요청") {
            When("자신의 공개 시간표를 조회하는 요청") {
                val data = timeTableService.getTimeTableById(
                    userEmail = userOne.email,
                    timeTableId = timeTableOne.id!!,
                )

                Then("시간표를 조회한다") {
                    data.id shouldBe timeTableOne.id!!
                    data.scheduleList shouldBe timeTableOne.schedules
                }
            }

            When("자신의 비공개 시간표를 조회하는 요청") {
                val data = timeTableService.getTimeTableById(
                    userEmail = userOne.email,
                    timeTableId = timeTableTwo.id!!,
                )

                Then("시간표를 조회한다") {
                    data.id shouldBe timeTableTwo.id!!
                    data.scheduleList shouldBe timeTableTwo.schedules
                }
            }

            xWhen("친구의 공개 시간표를 조회하는 요청") {}

            When("친구의 비공개 시간표를 조회하는 요청") {
                val error = shouldThrow<ConditionConflictException> {
                    timeTableService.getTimeTableById(
                        userEmail = userTwo.email,
                        timeTableId = timeTableThree.id!!,
                    )
                }

                Then("에러") {
                    error.log shouldBe "해당 시간표를 볼 수 있는 권한이 없습니다."
                }
            }

            xWhen("친구가 아닌 타인의 공개 시간표를 조회하는 요청") {}

            When("친구가 아닌 타인의 비공개 시간표를 조회하는 요청") {
                val error = shouldThrow<ConditionConflictException> {
                    timeTableService.getTimeTableById(
                        userEmail = userThree.email,
                        timeTableId = timeTableThree.id!!,
                    )
                }

                Then("에러") {
                    error.log shouldBe "해당 시간표를 볼 수 있는 권한이 없습니다."
                }
            }
        }

        // 시간표를 삭제하는 요청
        Given("시간표를 삭제하는 요청") {
            every { timeTableRepository.delete(any()) } returns Unit

            When("자신의 시간표를 삭제하는 요청") {
                val data = timeTableService.deleteMyTimeTableById(
                    userEmail = userOne.email,
                    timeTableId = timeTableOne.id!!,
                )

                Then("시간표를 삭제한다") {
                    data.id shouldBe timeTableOne.id!!
                }
            }

            When("타인의 시간표를 삭제하는 요청") {
                val error = shouldThrow<ConditionConflictException> {
                    timeTableService.deleteMyTimeTableById(
                        userEmail = userTwo.email,
                        timeTableId = timeTableThree.id!!,
                    )
                }

                Then("에러") {
                    error.log shouldBe "해당 시간표를 삭제할 수 있는 권한이 없습니다."
                }
            }
        }
    },
)

fun setScheduleTableRepository(
    timeTableOne: TimeTable,
    timeTableTwo: TimeTable,
    timeTableThree: TimeTable,
    scheduleRepository: ScheduleRepository,
) {
    every { scheduleRepository.findByTimeTableFetchDayOfWeekTimePairs(timeTableOne) } returns timeTableOne.schedules
    every { scheduleRepository.findByTimeTableFetchDayOfWeekTimePairs(timeTableTwo) } returns timeTableTwo.schedules
    every { scheduleRepository.findByTimeTableFetchDayOfWeekTimePairs(timeTableThree) } returns timeTableThree.schedules
}
