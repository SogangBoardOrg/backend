package com.kotlin.boardproject.docs

import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlin.boardproject.domain.schedule.domain.Course
import com.kotlin.boardproject.domain.schedule.domain.DayOfWeekTimePair
import com.kotlin.boardproject.domain.schedule.domain.YearAndSeason
import com.kotlin.boardproject.domain.schedule.repository.CourseRepository
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
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParameters
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
class CourseServiceImplTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var timeTableRepository: TimeTableRepository

    @Autowired
    private lateinit var courseRepository: CourseRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var tokenProvider: AuthTokenProvider

    private lateinit var user1: User

    private lateinit var user2: User

    private lateinit var accessToken: AuthToken

    private lateinit var accessToken2: AuthToken

    val statsEndPoint = "/api/v1/course"

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

        val courses = listOf(
            Course(
                title = "Introduction to Kotlin Programming",
                dayOfWeekTimePairs = mutableListOf(
                    DayOfWeekTimePair(
                        dayOfWeek = DayOfWeek.MONDAY,
                        startTime = LocalTime.of(9, 0),
                        endTime = LocalTime.of(10, 0),
                    ),
                ),
                credit = 3.0f,
                majorDepartment = "Computer Science",
                professor = "Dr. Smith",
                locaton = "Room 101",
                yearAndSeason = YearAndSeason(
                    year = 2021,
                    season = Seasons.SPRING,
                ),
                courseCode = "CS101",
            ),
            Course(
                title = "Introduction to Kotlin Programming",
                dayOfWeekTimePairs = mutableListOf(
                    DayOfWeekTimePair(
                        dayOfWeek = DayOfWeek.FRIDAY,
                        startTime = LocalTime.of(9, 0),
                        endTime = LocalTime.of(10, 0),
                    ),
                ),
                credit = 3.0f,
                majorDepartment = "Computer Science",
                professor = "Dr. Smith",
                locaton = "Room 101",
                yearAndSeason = YearAndSeason(
                    year = 2021,
                    season = Seasons.SPRING,
                ),
                courseCode = "CS102",
            ),
            Course(
                title = "Introduction to Kotlin Programming",
                dayOfWeekTimePairs = mutableListOf(
                    DayOfWeekTimePair(
                        dayOfWeek = DayOfWeek.MONDAY,
                        startTime = LocalTime.of(9, 0),
                        endTime = LocalTime.of(10, 0),
                    ),
                ),
                credit = 3.0f,
                majorDepartment = "Computer Science",
                professor = "Dr. Smith",
                locaton = "Room 101",
                yearAndSeason = YearAndSeason(
                    year = 2022,
                    season = Seasons.SPRING,
                ),
                courseCode = "CS103",
            ),
        )

        courseRepository.saveAllAndFlush(courses)
    }

    @Test
    @Rollback(true)
    fun 강의를_쿼리로_찾기() {
        // given
        val urlPoint = "/query"
        val finalUrl = "$statsEndPoint$urlPoint"

        val title = ""
        val major = ""
        val year = 2021
        val season = Seasons.SPRING
        val professor = ""
        val page = 0
        val size = 3
        val sort = ""

        val queryString =
            "?title=$title&major=$major&year=$year&season=$season&professor=$professor&page=$page&size=$size&sort=$sort"
        println("queryString: $queryString")
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.get(finalUrl + queryString).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "course-query",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestParameters(
                        parameterWithName("title").description("강의 제목").optional(),
                        parameterWithName("course-code").description("강의 코드").optional(),
                        parameterWithName("major").description("강의 학과").optional(),
                        parameterWithName("year").description("강의 연도").optional(),
                        parameterWithName("season").description("강의 계절").optional(),
                        parameterWithName("professor").description("교수 이름").optional(),
                        parameterWithName("page").description("페이지 번호").optional(),
                        parameterWithName("size").description("페이지 당 요소의 최대 개수").optional(),
                        parameterWithName("sort").description("정렬 기준").optional(),
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING)
                            .description("응답 상태를 나타내며, 성공, 실패, 오류 등의 값을 가질 수 있습니다."),
                        fieldWithPath("data.contents[]").type(JsonFieldType.ARRAY).description("강의 목록을 나타내는 배열"),
                        fieldWithPath("data.contents[].id").type(JsonFieldType.NUMBER).description("강의의 고유 식별자"),
                        fieldWithPath("data.contents[].title").type(JsonFieldType.STRING).description("강의의 제목"),
                        fieldWithPath("data.contents[].courseCode").type(JsonFieldType.STRING).description("강의의 코드"),
                        fieldWithPath("data.contents[].dayOfWeekTimePairs").type(JsonFieldType.ARRAY)
                            .description("강의가 있는 요일과 시간의 쌍 목록"),
                        fieldWithPath("data.contents[].dayOfWeekTimePairs[].dayOfWeek").type(JsonFieldType.STRING)
                            .description("강의가 있는 시간 요일"),
                        fieldWithPath("data.contents[].dayOfWeekTimePairs[].startTime").type(JsonFieldType.STRING)
                            .description("강의 시작 시간"),
                        fieldWithPath("data.contents[].dayOfWeekTimePairs[].endTime").type(JsonFieldType.STRING)
                            .description("강의가 끝나는 시간"),
                        fieldWithPath("data.contents[].credit").type(JsonFieldType.NUMBER).description("강의의 학점"),
                        fieldWithPath("data.contents[].majorDepartment").type(JsonFieldType.STRING)
                            .description("강의를 제공하는 학과"),
                        fieldWithPath("data.contents[].professor").type(JsonFieldType.STRING)
                            .description("강의를 가르치는 교수의 이름"),
                        fieldWithPath("data.contents[].location").type(JsonFieldType.STRING).description("강의가 열리는 위치"),
                        fieldWithPath("data.contents[].year").type(JsonFieldType.NUMBER).description("강의가 열리는 연도"),
                        fieldWithPath("data.contents[].season").type(JsonFieldType.STRING).description("강의가 열리는 계절"),
                        fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소의 수"),
                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지의 요소 수"),
                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 당 요소의 최대 개수"),
                    ),
                ),
            )

        // then
        courseRepository.findAll().size shouldBe 3
        courseRepository.findAll().map { it.yearAndSeason.year }.toSet() shouldBe setOf(2021, 2022)
        courseRepository.findAll().map { it.yearAndSeason.season }.toSet() shouldBe setOf(Seasons.SPRING)
        courseRepository.findAll().groupBy { it.yearAndSeason }.size shouldBe 2
    }

    @Test
    @Rollback(true)
    fun 강의를_id로_찾기() {
        // given
        val urlPoint = "/{postId}"
        val finalUrl = "$statsEndPoint$urlPoint"

        val result = mockMvc.perform(
            RestDocumentationRequestBuilders
                .get(finalUrl, courseRepository.findAll()[0].id!!)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success")))
            .andDo(
                MockMvcRestDocumentation.document(
                    "course-id",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING)
                            .description("응답 상태를 나타내며, 성공, 실패, 오류 등의 값을 가질 수 있습니다."),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("강의의 고유 식별자"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("강의의 제목"),
                        fieldWithPath("data.courseCode").type(JsonFieldType.STRING).description("강의의 코드"),
                        fieldWithPath("data.dayOfWeekTimePairs").type(JsonFieldType.ARRAY)
                            .description("강의가 있는 요일과 시간의 쌍 목록"),
                        fieldWithPath("data.dayOfWeekTimePairs[].dayOfWeek").type(JsonFieldType.STRING)
                            .description("강의가 있는 시간 요일"),
                        fieldWithPath("data.dayOfWeekTimePairs[].startTime").type(JsonFieldType.STRING)
                            .description("강의 시작 시간"),
                        fieldWithPath("data.dayOfWeekTimePairs[].endTime").type(JsonFieldType.STRING)
                            .description("강의가 끝나는 시간"),
                        fieldWithPath("data.credit").type(JsonFieldType.NUMBER).description("강의의 학점"),
                        fieldWithPath("data.majorDepartment").type(JsonFieldType.STRING).description("강의를 제공하는 학과"),
                        fieldWithPath("data.professor").type(JsonFieldType.STRING).description("강의를 가르치는 교수의 이름"),
                        fieldWithPath("data.location").type(JsonFieldType.STRING).description("강의가 열리는 위치"),
                        fieldWithPath("data.year").type(JsonFieldType.NUMBER).description("강의가 열리는 연도"),
                        fieldWithPath("data.season").type(JsonFieldType.STRING).description("강의가 열리는 계절"),
                    ),
                ),
            )
    }
}
