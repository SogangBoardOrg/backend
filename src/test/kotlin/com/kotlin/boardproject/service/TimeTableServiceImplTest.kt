package com.kotlin.boardproject.service

import com.kotlin.boardproject.domain.schedule.domain.Schedule
import com.kotlin.boardproject.domain.schedule.domain.TimeTable
import com.kotlin.boardproject.domain.schedule.domain.YearAndSeason
import com.kotlin.boardproject.domain.schedule.dto.AddScheduleRequestDto
import com.kotlin.boardproject.domain.schedule.dto.CreateTimeTableRequestDto
import com.kotlin.boardproject.domain.schedule.dto.DayOfWeekTimePairDto
import com.kotlin.boardproject.domain.schedule.repository.CourseRepository
import com.kotlin.boardproject.domain.schedule.repository.ScheduleRepository
import com.kotlin.boardproject.domain.schedule.repository.TimeTableRepository
import com.kotlin.boardproject.domain.schedule.service.ScheduleServiceImpl
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
import java.time.DayOfWeek
import java.time.LocalTime

class TimeTableServiceImplTest : BehaviorSpec(
    {
        isolationMode = IsolationMode.InstancePerTest

        val (userOne, userTwo, userThree) = makeUser()
        val (timeTableOne, timeTableTwo, timeTableThree) = makeTimeTable(userOne)

        val userRepository: UserRepository = mockk()
        val timeTableRepository: TimeTableRepository = mockk()
        val scheduleRepository: ScheduleRepository = mockk()
        val courseRepository: CourseRepository = mockk()

        val timeTableService = TimeTableServiceImpl(
            userRepository = userRepository,
            timeTableRepository = timeTableRepository,
            scheduleRepository = scheduleRepository,
        )

        val scheduleService = ScheduleServiceImpl(
            userRepository = userRepository,
            timeTableRepository = timeTableRepository,
            scheduleRepository = scheduleRepository,
            courseRepository = courseRepository,
        )

        connectTimeTableAndSchedule(timeTableOne)
        connectTimeTableAndSchedule(timeTableTwo)
        connectTimeTableAndSchedule(timeTableThree)

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
                }
            }

            When("자신의 비공개 시간표를 조회하는 요청") {
                val data = timeTableService.getTimeTableById(
                    userEmail = userOne.email,
                    timeTableId = timeTableTwo.id!!,
                )

                Then("시간표를 조회한다") {
                    data.id shouldBe timeTableTwo.id!!
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

        // 자신의 시간표를 조회하는 요청
        Given("자신의 시간표 조회") {
            When("자신의 시간표 조회") {
                val data = timeTableService.getMyTimeTableList(userOne.email)

                Then("자신의 시간표를 조회한다") {
                    data.timeTableMap.size shouldBe 2
                    data.timeTableMap[YearAndSeason(2021, Seasons.SPRING)]?.size shouldBe 2
                    data.timeTableMap[YearAndSeason(2023, Seasons.SPRING)]?.size shouldBe 1
                }
            }
        }

        // 스케쥴을 추가하는 요청
        Given("스케쥴을 추가하는 요청") {
            When("스케쥴을 정상적으로 추가하는 요청") {
                every { scheduleRepository.save(any()) } returns Schedule(
                    id = 4L,
                    title = "스케쥴1",
                    memo = "메모1",
                    timeTable = timeTableOne,
                    dayOfWeekTimePairs = mutableListOf(),
                    alphabetGrade = null,
                    credit = 3.0f,
                    isMajor = true,
                    professor = "김교수",
                    location = "A동",
                    majorDepartment = "컴퓨터공학과",
                )
                val data =
                    scheduleService.addSchedule(
                        userEmail = userOne.email,
                        timeTableId = timeTableOne.id!!,
                        addScheduleRequestDto = AddScheduleRequestDto(
                            title = "스케쥴1",
                            memo = "메모1",
                            alphabetGrade = null,
                            credit = 3.0f,
                            isMajor = true,
                            majorDepartment = "컴퓨터공학과",
                            professor = "김교수",
                            location = "A동",
                            courseId = null,
                            dayOfWeekTimePairs = listOf(
                                DayOfWeekTimePairDto(
                                    dayOfWeek = DayOfWeek.MONDAY,
                                    startTime = LocalTime.of(11, 0),
                                    endTime = LocalTime.of(12, 0),
                                ),
                                DayOfWeekTimePairDto(
                                    dayOfWeek = DayOfWeek.TUESDAY,
                                    startTime = LocalTime.of(12, 0),
                                    endTime = LocalTime.of(13, 0),
                                ),
                                DayOfWeekTimePairDto(
                                    dayOfWeek = DayOfWeek.TUESDAY,
                                    startTime = LocalTime.of(11, 0),
                                    endTime = LocalTime.of(12, 0),
                                ),
                            ),
                        ),
                    )

                Then("스케쥴을 추가한다") {
                }
            }

            xWhen("없는 시간표에 스케쥴을 추가하는 요청") {
            }

            xWhen("타인의 시간표에 스케쥴을 추가하는 요청") {
            }

            When("추가하는 시간표끼리 겹치는 시간이 있는 요청") {
                val error = shouldThrow<ConditionConflictException> {
                    scheduleService.addSchedule(
                        userEmail = userOne.email,
                        timeTableId = timeTableOne.id!!,
                        addScheduleRequestDto = AddScheduleRequestDto(
                            title = "스케쥴1",
                            memo = "메모1",
                            alphabetGrade = null,
                            credit = 3.0f,
                            isMajor = true,
                            majorDepartment = "컴퓨터공학과",
                            professor = "김교수",
                            location = "A동",
                            courseId = null,
                            dayOfWeekTimePairs = listOf(
                                DayOfWeekTimePairDto(
                                    dayOfWeek = DayOfWeek.MONDAY,
                                    startTime = LocalTime.of(1, 0),
                                    endTime = LocalTime.of(3, 0),
                                ),
                                DayOfWeekTimePairDto(
                                    dayOfWeek = DayOfWeek.MONDAY,
                                    startTime = LocalTime.of(2, 0),
                                    endTime = LocalTime.of(4, 0),
                                ),
                            ),
                        ),
                    )
                }

                Then("에러") {
                    error.log shouldBe "시간이 올바르지 않습니다."
                }
            }

            When("시간표에 이미 있는 시간에 스케쥴을 추가하는 요청") {
                val error = shouldThrow<ConditionConflictException> {
                    scheduleService.addSchedule(
                        userEmail = userOne.email,
                        timeTableId = timeTableOne.id!!,
                        addScheduleRequestDto = AddScheduleRequestDto(
                            title = "스케쥴1",
                            memo = "메모1",
                            alphabetGrade = null,
                            credit = 3.0f,
                            isMajor = true,
                            majorDepartment = "컴퓨터공학과",
                            professor = "김교수",
                            location = "A동",
                            courseId = null,
                            dayOfWeekTimePairs = listOf(
                                DayOfWeekTimePairDto(
                                    dayOfWeek = DayOfWeek.MONDAY,
                                    startTime = LocalTime.of(1, 0),
                                    endTime = LocalTime.of(3, 0),
                                ),
                                DayOfWeekTimePairDto(
                                    dayOfWeek = DayOfWeek.TUESDAY,
                                    startTime = LocalTime.of(1, 0),
                                    endTime = LocalTime.of(3, 0),
                                ),
                                DayOfWeekTimePairDto(
                                    dayOfWeek = DayOfWeek.TUESDAY,
                                    startTime = LocalTime.of(10, 0),
                                    endTime = LocalTime.of(11, 0),
                                ),
                            ),
                        ),
                    )
                }

                Then("이미 시간이 존재하는 에러") {
                    error.log shouldBe "시간표에 이미 존재하는 시간입니다."
                }
            }
        }

        // 스케쥴을 삭제하는 요청
    },
)
