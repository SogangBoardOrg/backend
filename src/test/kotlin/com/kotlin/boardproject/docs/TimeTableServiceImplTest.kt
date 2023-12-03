package com.kotlin.boardproject.docs

import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlin.boardproject.domain.schedule.domain.DayOfWeekTimePair
import com.kotlin.boardproject.domain.schedule.domain.Schedule
import com.kotlin.boardproject.domain.schedule.domain.TimeTable
import com.kotlin.boardproject.domain.schedule.domain.YearAndSeason
import com.kotlin.boardproject.domain.schedule.dto.AddScheduleRequestDto
import com.kotlin.boardproject.domain.schedule.dto.CreateTimeTableRequestDto
import com.kotlin.boardproject.domain.schedule.dto.DayOfWeekTimePairDto
import com.kotlin.boardproject.domain.schedule.dto.DeleteScheduleRequestDto
import com.kotlin.boardproject.domain.schedule.repository.ScheduleRepository
import com.kotlin.boardproject.domain.schedule.repository.TimeTableRepository
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.global.enums.ProviderType
import com.kotlin.boardproject.global.enums.Role
import com.kotlin.boardproject.global.enums.Seasons
import com.kotlin.boardproject.global.util.AuthToken
import com.kotlin.boardproject.global.util.AuthTokenProvider
import io.kotest.matchers.shouldBe
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.Date
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TimeTableServiceImplTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var timeTableRepository: TimeTableRepository

    @Autowired
    private lateinit var scheduleRepository: ScheduleRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var tokenProvider: AuthTokenProvider

    private lateinit var user1: User

    private lateinit var user2: User

    private lateinit var accessToken: AuthToken

    private lateinit var accessToken2: AuthToken

    val statsEndPoint = "/api/v1/timetable"

    @BeforeEach
    fun default_setting() {
        val user: User =
            User(
                id = UUID.randomUUID(),
                email = "test@test.com",
                password = "test1234!",
                nickname = "test",
                providerType = ProviderType.LOCAL,
                role = Role.ROLE_VERIFIED_USER,
            )
        user1 = userRepository.saveAndFlush(user)

        accessToken = tokenProvider.createAuthToken(
            email = "test@test.com",
            expiry = Date(Date().time + 6000000),
            role = Role.ROLE_VERIFIED_USER.code,
        )

        val userTwo: User = User(
            id = UUID.randomUUID(),
            email = "test2@test.com",
            password = "test1234!",
            nickname = "test2",
            providerType = ProviderType.LOCAL,
            role = Role.ROLE_VERIFIED_USER,
        )
        user2 = userRepository.saveAndFlush(userTwo)

        accessToken2 = tokenProvider.createAuthToken(
            email = "test2@test.com",
            expiry = Date(Date().time + 6000000),
            role = Role.ROLE_VERIFIED_USER.code,
        )
    }

    @Test
    @Rollback(true)
    fun 시간표_정상_등록() {
        // given
        val urlPoint = ""
        val finalUrl = "$statsEndPoint$urlPoint"

        val title = "title_test"
        val content = "content_test"

        val createTimeTableDto = CreateTimeTableRequestDto(
            year = 2021,
            season = Seasons.SPRING,
            title = "test",
            isPublic = true,
        )

        val timetableDtoString = objectMapper.writeValueAsString(createTimeTableDto)
        // when

        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post(finalUrl).contentType(MediaType.APPLICATION_JSON)
                .content(timetableDtoString).header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "timetable-create",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description(
                            "인증을 위한 Access 토큰, 시간표를 작성하는 유저를 식별하기 위해서 반드시 필요함",
                        ),
                    ),
                    requestFields(
                        fieldWithPath("year").description("연도"),
                        fieldWithPath("season").description("계절"),
                        fieldWithPath("title").description("시간표 제목"),
                        fieldWithPath("isPublic").description("공개 여부"),
                    ),
                    responseFields(
                        fieldWithPath("data.id").description("시간표 번호"),
                        fieldWithPath("status").description("성공 여부"),
                    ),
                ),
            )

        // then
        timeTableRepository.findAll()[0].id!! shouldBe 1
    }

    @Test
    @Rollback(true)
    fun id로_시간표_찾기() {
        // given
        val urlPoint = "/{timeTableId}"
        val finalUrl = "$statsEndPoint$urlPoint"

        val timeTable = timeTableRepository.saveAndFlush(
            TimeTable(
                title = "test",
                isPublic = true,
                user = user1,
                yearAndSeason = YearAndSeason(
                    year = 2021,
                    season = Seasons.SPRING,
                ),
                schedules = mutableListOf(),
                isMain = false,
            ),
        )

        val schedule = scheduleRepository.saveAndFlush(
            Schedule(
                title = "test",
                memo = "test",
                alphabetGrade = null,
                credit = 3.0f,
                isMajor = false,
                majorDepartment = "test",
                professor = "test",
                location = "test",
                course = null,
                dayOfWeekTimePairs = listOf(
                    DayOfWeekTimePair(
                        dayOfWeek = DayOfWeek.MONDAY,
                        startTime = LocalTime.of(1, 0),
                        endTime = LocalTime.of(3, 0),
                    ),
                ).toMutableList(),
                timeTable = timeTable,
            ),
        )

        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.get(finalUrl, timeTable.id!!).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "timetable-get-by-id",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description(
                            "인증을 위한 Access 토큰, 시간표를 조회하는 유저를 식별하기 위해서 반드시 필요함",
                        ),
                    ),
                    responseFields(
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("시간표 ID"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("시간표 제목"),
                        fieldWithPath("data.isMain").type(JsonFieldType.BOOLEAN).description("메인 시간표 여부"),
                        fieldWithPath("data.isPublic").type(JsonFieldType.BOOLEAN).description("공개 여부"),
                        fieldWithPath("data.year").type(JsonFieldType.NUMBER).description("연도"),
                        fieldWithPath("data.season").type(JsonFieldType.STRING).description("계절"),
                        fieldWithPath("data.schedules").type(JsonFieldType.ARRAY).description("시간표에 등록된 일정"),
                        // ScheduleResponseDto 내부 필드
                        fieldWithPath("data.schedules[].id").type(JsonFieldType.NUMBER).description("일정 ID"),
                        fieldWithPath("data.schedules[].title").type(JsonFieldType.STRING).description("일정 제목"),
                        fieldWithPath("data.schedules[].memo").type(JsonFieldType.STRING).description("메모"),
                        fieldWithPath("data.schedules[].dayOfWeekTimePairs").type(JsonFieldType.ARRAY)
                            .description("요일 및 시간 쌍"),
                        // DayOfWeekTimePairDto 내부 필드
                        fieldWithPath("data.schedules[].dayOfWeekTimePairs[].dayOfWeek").type(JsonFieldType.STRING)
                            .description("요일"),
                        fieldWithPath("data.schedules[].dayOfWeekTimePairs[].startTime").type(JsonFieldType.STRING)
                            .description("시작 시간"),
                        fieldWithPath("data.schedules[].dayOfWeekTimePairs[].endTime").type(JsonFieldType.STRING)
                            .description("종료 시간"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                    ),
                ),
            )

        // then
        timeTableRepository.findAll()[0].id!! shouldBe timeTable.id!!
    }

    @Test
    @Rollback(true)
    fun 시간표_삭제() {
        // given
        val urlPoint = "/{timeTableId}"
        val finalUrl = "$statsEndPoint$urlPoint"

        val timeTable = timeTableRepository.saveAndFlush(
            TimeTable(
                title = "test",
                isPublic = true,
                user = user1,
                yearAndSeason = YearAndSeason(
                    year = 2021,
                    season = Seasons.SPRING,
                ),
                schedules = mutableListOf(),
                isMain = false,
            ),
        )

        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.delete(finalUrl, timeTable.id!!).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "timetable-delete",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description(
                            "인증을 위한 Access 토큰, 시간표를 삭제하는 유저를 식별하기 위해서 반드시 필요함",
                        ),
                    ),
                    responseFields(
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("삭제된 시간표 번호"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                    ),
                ),
            )

        // then
        timeTableRepository.findAll().size shouldBe 0
    }

    @Test
    @Rollback(true)
    fun 스케쥴_추가() {
        // given
        val urlPoint = "/{timeTableId}/schedule"
        val finalUrl = "$statsEndPoint$urlPoint"

        val timeTable = timeTableRepository.saveAndFlush(
            TimeTable(
                title = "test",
                isPublic = true,
                user = user1,
                yearAndSeason = YearAndSeason(
                    year = 2021,
                    season = Seasons.SPRING,
                ),
                schedules = mutableListOf(),
                isMain = false,
            ),
        )

        val addScheduleRequestDto = AddScheduleRequestDto(
            title = "test",
            memo = "test",
            alphabetGrade = null,
            credit = 3.0f,
            isMajor = false,
            majorDepartment = "test",
            professor = "test",
            location = "test",
            courseId = null,
            dayOfWeekTimePairs = listOf(
                DayOfWeekTimePairDto(
                    dayOfWeek = DayOfWeek.MONDAY,
                    startTime = LocalTime.of(1, 0),
                    endTime = LocalTime.of(3, 0),
                ),
            ),
        )

        val addScheduleRequestDtoString = objectMapper.writeValueAsString(addScheduleRequestDto)

        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post(finalUrl, timeTable.id!!).contentType(MediaType.APPLICATION_JSON)
                .content(addScheduleRequestDtoString)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "timetable-add-schedule",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description(
                            "인증을 위한 Access 토큰, 시간표에 일정을 추가하는 유저를 식별하기 위해서 반드시 필요함",
                        ),
                    ),
                    requestFields(
                        fieldWithPath("title").description("일정의 제목").type(JsonFieldType.STRING),
                        fieldWithPath("memo").description("일정에 대한 메모").type(JsonFieldType.STRING),
                        fieldWithPath("alphabetGrade").description("알파벳 등급 (옵션)").type(JsonFieldType.STRING).optional(),
                        fieldWithPath("credit").description("학점").type(JsonFieldType.NUMBER),
                        fieldWithPath("isMajor").description("주 전공 여부").type(JsonFieldType.BOOLEAN),
                        fieldWithPath("majorDepartment").description("주 전공 학과").type(JsonFieldType.STRING),
                        fieldWithPath("professor").description("교수님 이름").type(JsonFieldType.STRING),
                        fieldWithPath("location").description("수업 장소").type(JsonFieldType.STRING),
                        fieldWithPath("courseId").description("과목 ID (옵션)").type(JsonFieldType.NUMBER).optional(),
                        fieldWithPath("dayOfWeekTimePairs").description("수업 시간 및 요일").type(JsonFieldType.ARRAY),
                        fieldWithPath("dayOfWeekTimePairs[].dayOfWeek").description("요일").type(JsonFieldType.STRING),
                        fieldWithPath("dayOfWeekTimePairs[].startTime").description("수업 시작 시간")
                            .type(JsonFieldType.STRING),
                        fieldWithPath("dayOfWeekTimePairs[].endTime").description("수업 종료 시간")
                            .type(JsonFieldType.STRING),
                    ),
                    responseFields(
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("추가된 일정 번호"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                    ),
                ),
            )

        // then
        scheduleRepository.findAll().size shouldBe 1
    }

    @Test
    @Rollback(true)
    fun 스케쥴_삭제() {
        // given
        val urlPoint = "/{timeTableId}/schedule"
        val finalUrl = "$statsEndPoint$urlPoint"

        val timeTable = timeTableRepository.saveAndFlush(
            TimeTable(
                title = "test",
                isPublic = true,
                user = user1,
                yearAndSeason = YearAndSeason(
                    year = 2021,
                    season = Seasons.SPRING,
                ),
                schedules = mutableListOf(),
                isMain = false,
            ),
        )

        val schedule = scheduleRepository.saveAndFlush(
            Schedule(
                title = "test",
                memo = "test",
                alphabetGrade = null,
                credit = 3.0f,
                isMajor = false,
                majorDepartment = "test",
                professor = "test",
                location = "test",
                course = null,
                dayOfWeekTimePairs = listOf(
                    DayOfWeekTimePair(
                        dayOfWeek = DayOfWeek.MONDAY,
                        startTime = LocalTime.of(1, 0),
                        endTime = LocalTime.of(3, 0),
                    ),
                ).toMutableList(),
                timeTable = timeTable,
            ),
        )

        val deleteScheduleRequestDto = DeleteScheduleRequestDto(
            scheduleId = schedule.id!!,
        )

        val deleteScheduleRequestDtoString = objectMapper.writeValueAsString(deleteScheduleRequestDto)

        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.delete(finalUrl, timeTable.id!!).contentType(MediaType.APPLICATION_JSON)
                .content(deleteScheduleRequestDtoString)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "timetable-delete-schedule",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description(
                            "인증을 위한 Access 토큰, 시간표에 일정을 추가하는 유저를 식별하기 위해서 반드시 필요함",
                        ),
                    ),
                    requestFields(
                        fieldWithPath("scheduleId").type(JsonFieldType.NUMBER).description("일정의 ID"),
                    ),
                    responseFields(
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("삭제된 일정 번호"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                    ),
                ),
            )

        // then
        scheduleRepository.findAll().size shouldBe 0
        timeTableRepository.findAll().size shouldBe 1
        timeTableRepository.findAll()[0].schedules.size shouldBe 0
    }
}
