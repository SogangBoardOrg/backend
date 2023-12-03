package com.kotlin.boardproject.docs

import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.global.enums.ProviderType
import com.kotlin.boardproject.global.enums.Role
import com.kotlin.boardproject.global.util.AuthToken
import com.kotlin.boardproject.global.util.AuthTokenProvider
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthServiceImplTest {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var tokenProvider: AuthTokenProvider

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var accessToken: AuthToken

    @BeforeEach
    fun default_setting() {
        val user: User = User(
            id = UUID.randomUUID(),
            email = "test@test.com",
            password = "test1234!",
            nickname = "test",
            providerType = ProviderType.LOCAL,
            role = Role.ROLE_VERIFIED_USER,
        )
        userRepository.saveAndFlush(user)

        accessToken = tokenProvider.createAuthToken(
            email = "test@test.com",
            expiry = Date(Date().time + 6000000),
            role = Role.ROLE_VERIFIED_USER.code,
        )
    }

    @Test
    fun 중복_이메일_체크_중복_O() {
        val finalUrl = "/api/v1/auth/duplicate-email?email=test@test.com"

        val result = mockMvc.perform(
            RestDocumentationRequestBuilders
                .get(finalUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data", true).exists())
            .andExpect(content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "duplicate-email-is-duplicate",
                    preprocessRequest(Preprocessors.prettyPrint()),
                    preprocessResponse(Preprocessors.prettyPrint()),
                    requestHeaders(),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                        fieldWithPath("data").type(JsonFieldType.BOOLEAN).description("중복 여부 (true: 중복, false: 중복 아님)"),
                    ),
                ),
            )
        userRepository.findByEmail("test@test.com") shouldNotBe null
    }

    @Test
    fun 중복_이메일_체크_중복_X() {
        val finalUrl = "/api/v1/auth/duplicate-email?email=fail@test.com"

        val result = mockMvc.perform(
            RestDocumentationRequestBuilders
                .get(finalUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data", false).exists())
            .andExpect(content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "duplicate-email-not-duplicate",
                    preprocessRequest(Preprocessors.prettyPrint()),
                    preprocessResponse(Preprocessors.prettyPrint()),
                    requestHeaders(),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                        fieldWithPath("data").type(JsonFieldType.BOOLEAN).description("중복 여부 (true: 중복, false: 중복 아님)"),
                    ),
                ),
            )

        userRepository.findByEmail("fail@test.com") shouldBe null
    }

    @Test
    fun 중복_닉네임_체크_중복_O() {
        val finalUrl = "/api/v1/auth/duplicate-nickname?nickname=test"

        val result = mockMvc.perform(
            RestDocumentationRequestBuilders
                .get(finalUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data", true).exists())
            .andExpect(content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "duplicate-nickname-is-duplicate",
                    preprocessRequest(Preprocessors.prettyPrint()),
                    preprocessResponse(Preprocessors.prettyPrint()),
                    requestHeaders(),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                        fieldWithPath("data").type(JsonFieldType.BOOLEAN).description("중복 여부 (true: 중복, false: 중복 아님)"),
                    ),
                ),
            )

        userRepository.findByNickname("test") shouldNotBe null
    }

    @Test
    fun 중복_닉네임_체크_중복_X() {
        val finalUrl = "/api/v1/auth/duplicate-nickname?nickname=fail"

        val result = mockMvc.perform(
            RestDocumentationRequestBuilders
                .get(finalUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data", false).exists())
            .andExpect(content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "duplicate-nickname-not-duplicate",
                    preprocessRequest(Preprocessors.prettyPrint()),
                    preprocessResponse(Preprocessors.prettyPrint()),
                    requestHeaders(),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                        fieldWithPath("data").type(JsonFieldType.BOOLEAN).description("중복 여부 (true: 중복, false: 중복 아님)"),
                    ),
                ),
            )

        userRepository.findByNickname("fail") shouldBe null
    }

    @Test
    fun 내_정보_가져오기() {
        val finalUrl = "/api/v1/my/info"

        // when

        val result = mockMvc.perform(
            RestDocumentationRequestBuilders
                .get(finalUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(status().isOk)
            .andExpect(content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "view-my-info",
                    preprocessRequest(Preprocessors.prettyPrint()),
                    preprocessResponse(Preprocessors.prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("인증을 위한 Access 토큰, 글을 쓰는 유저를 식별하기 위해서 반드시 필요함"),
                    ),

                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                        fieldWithPath("data.id").type(JsonFieldType.STRING).description("유저 아이디"),
                        fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("유저 이메일"),
                        fieldWithPath("data.role").type(JsonFieldType.STRING).description("유저 역할"),
                        fieldWithPath("data.accessToken").type(JsonFieldType.STRING).optional()
                            .description("안쓰는 필드"),
                        fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).optional()
                            .description("안쓰는 필드"),
                    ),
                ),
            )
    }

    @Test
    fun 유저_등록_정상() {
        // given
        // every { userRepository.save(any()) } returns user

        // when
        // val result = authService.saveUser(UserSignUpDto("test@test.com", "test", "test1234!"))

        // then
        // verify(exactly = 1) { passwordEncoder.encode("test1234!") }
        // verify(exactly = 1) { userRepository.save(any()) }
        // Assertions.assertThat(user.id).isEqualTo(result)
    }
}
