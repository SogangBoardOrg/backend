package com.kotlin.boardproject.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlin.boardproject.auth.AuthTokenProvider
import com.kotlin.boardproject.auth.ProviderType
import com.kotlin.boardproject.common.enums.NormalType
import com.kotlin.boardproject.common.enums.Role
import com.kotlin.boardproject.dto.CreateNormalPostRequestDto
import com.kotlin.boardproject.model.User
import com.kotlin.boardproject.repository.NormalPostRepository
import com.kotlin.boardproject.repository.UserRepository
import io.kotest.matchers.shouldBe
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("local")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostServiceImplTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var normalPostRepository: NormalPostRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var tokenProvider: AuthTokenProvider

    private lateinit var writer: User

    val statsEndPoint = "/api/v1/post"

    @BeforeAll
    fun start() {
        val user: User = User(
            id = UUID.randomUUID(),
            email = "test@test.com",
            password = "test1234!",
            username = "test",
            providerType = ProviderType.LOCAL,
            role = Role.ROLE_VERIFIED_USER,
        )
        writer = userRepository.save(user)
    }

    @AfterEach
    fun cleardb() {
    }

    @Test
    fun 자유게시판_글_정상_등록() {
        // given

        var title = "title_test"
        var content = "content_test"

        val createNormalPostRequestDto = CreateNormalPostRequestDto(
            title = title,
            content = content,
            isAnon = true,
            commentOn = true,
            normalType = NormalType.FREE,
        )

        val post = createNormalPostRequestDto.toPost(writer)

        normalPostRepository.save(post)

        val accessToken = tokenProvider.createAuthToken(
            email = "test@test.com",
            expiry = Date(Date().time + 600000),
            role = Role.ROLE_VERIFIED_USER.code,
        )

        val normalPostString = objectMapper.writeValueAsString(createNormalPostRequestDto)
        val urlPoint = "/create"
        val finalUrl = "$statsEndPoint$urlPoint"
        // when

        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post(finalUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(normalPostString)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success")))
            .andDo(
                document(
                    "normal-post-create",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("title").description("제목"),
                        fieldWithPath("content").description("글 내용"),
                        fieldWithPath("isAnon").description("익명 여부"),
                        fieldWithPath("commentOn").description("댓글 여부"),
                        fieldWithPath("normalType").description("일반 포스트의 게시판 타입"),
                    ),
                    responseFields(
                        fieldWithPath("data.id").description("게시글 번호"),
                        fieldWithPath("status").description("성공 여부"),
                    ),
                ),
            )

        // then
        var basePosts = normalPostRepository.findAll()

        // basePosts.size shouldBe 1
        basePosts[0].title shouldBe "title_test"
        basePosts[0].content shouldBe "content_test"
    }
}
