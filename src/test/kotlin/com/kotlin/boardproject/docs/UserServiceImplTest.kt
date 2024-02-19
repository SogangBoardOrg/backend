package com.kotlin.boardproject.docs

import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.domain.user.dto.NicknameChangeDto
import com.kotlin.boardproject.domain.user.dto.ProfileImageUrlChangeDto
import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.domain.user.service.UserService
import com.kotlin.boardproject.global.enums.ProviderType
import com.kotlin.boardproject.global.enums.Role
import com.kotlin.boardproject.global.util.AuthToken
import com.kotlin.boardproject.global.util.AuthTokenProvider
import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
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
class UserServiceImplTest {
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var tokenProvider: AuthTokenProvider

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var user: User

    private lateinit var accessToken: AuthToken

    val statsEndPoint = "/api/v1/user"

    @BeforeEach
    fun setUp() {
        user = User(
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
            role = Role.ROLE_VERIFIED_USER.code,
            expiry = Date(System.currentTimeMillis() + 6000000), // 예시로 10분 후 만료 설정
        )
    }

    @Test
    fun `프로필 이미지 변경 성공`() {
        val urlPoint = "/profile-image"
        val finalUrl = "$statsEndPoint$urlPoint"

        val profileImageUrl = "http://new-image-url.com"

        val result = mockMvc.perform(
            put(finalUrl).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ProfileImageUrlChangeDto(profileImageUrl)))
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(status().isOk)
            .andExpect(content().string(containsString("success")))
            .andDo(
                document(
                    "change-profile-image",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("인증을 위한 Access 토큰, 댓글을 쓰는 유저를 위해 필요함"),
                    ),
                    requestFields(
                        fieldWithPath("profileImageUrl").description("새 프로필 이미지 URL"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("응답 상태"),
                        fieldWithPath("data").description("응답 메시지"),
                    ),
                ),
            )
    }

    @Test
    fun `닉네임 변경 성공`() {
        val urlPoint = "/nickname"
        val finalUrl = "$statsEndPoint$urlPoint"

        val newNickname = "newNickname"
        val userEmail = "user@test.com"

        val result = mockMvc.perform(
            put(finalUrl)
                .content(objectMapper.writeValueAsString(NicknameChangeDto(newNickname))).contentType(
                    MediaType.APPLICATION_JSON,
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(status().isOk)
            .andExpect(content().string(containsString("success")))
            .andDo(
                document(
                    "change-nickname",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("인증을 위한 Access 토큰, 댓글을 쓰는 유저를 위해 필요함"),
                    ),
                    requestFields(
                        fieldWithPath("nickname").description("새 닉네임"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("응답 상태"),
                        fieldWithPath("data").description("응답 메시지"),
                    ),
                ),
            )
    }
}
