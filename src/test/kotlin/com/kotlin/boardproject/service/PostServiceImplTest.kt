package com.kotlin.boardproject.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlin.boardproject.auth.AuthToken
import com.kotlin.boardproject.auth.AuthTokenProvider
import com.kotlin.boardproject.auth.ProviderType
import com.kotlin.boardproject.common.enums.NormalType
import com.kotlin.boardproject.common.enums.Role
import com.kotlin.boardproject.dto.CreateNormalPostRequestDto
import com.kotlin.boardproject.dto.EditNormalPostRequestDto
import com.kotlin.boardproject.model.NormalPost
import com.kotlin.boardproject.model.User
import com.kotlin.boardproject.repository.NormalPostRepository
import com.kotlin.boardproject.repository.UserRepository
import io.kotest.matchers.shouldBe
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.AfterEach
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
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
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

    private lateinit var accessToken: AuthToken

    val statsEndPoint = "/api/v1/post"

    @BeforeEach
    fun default_setting() {
        val user: User = User(
            id = UUID.randomUUID(),
            email = "test@test.com",
            password = "test1234!",
            username = "test",
            providerType = ProviderType.LOCAL,
            role = Role.ROLE_VERIFIED_USER,
        )
        writer = userRepository.saveAndFlush(user)

        accessToken = tokenProvider.createAuthToken(
            email = "test@test.com",
            expiry = Date(Date().time + 6000000),
            role = Role.ROLE_VERIFIED_USER.code,
        )
    }

    @AfterEach
    fun cleardb() {
        normalPostRepository.deleteAll()
    }

    @Test
    @Rollback(true)
    fun 일반게시판_글_정상_등록() {
        // given
        val urlPoint = "/create"
        val finalUrl = "$statsEndPoint$urlPoint"

        val title = "title_test"
        val content = "content_test"

        val createNormalPostRequestDto = CreateNormalPostRequestDto(
            title = title,
            content = content,
            isAnon = true,
            commentOn = true,
            normalType = NormalType.FREE,
        )

        val normalPostString = objectMapper.writeValueAsString(createNormalPostRequestDto)
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
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("인증을 위한 Access 토큰 글을 쓰는 유저를 식별하기 위해서 반드시 필요함"),
                    ),
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

        val basePosts = normalPostRepository.findAll()

        basePosts.size shouldBe 1
        basePosts[0].title shouldBe "title_test"
        basePosts[0].content shouldBe "content_test"
        basePosts[0].writer.email shouldBe "test@test.com"
    }

    @Test
    @Rollback(true)
    fun 일반게시판_글_수정() {
        // given
        val urlPoint = "/{postId}"
        val finalUrl = "$statsEndPoint$urlPoint"

        val title = "title_test"
        val content = "content_test"

        val new_title = "new_title_test"
        val new_content = "new_content_test"

        val post = normalPostRepository.saveAndFlush(
            NormalPost(
                title = title,
                content = content,
                isAnon = true,
                commentOn = true,
                writer = writer,
                normalType = NormalType.FREE,
            ),
        )

        val editNormalPostRequestDto = EditNormalPostRequestDto(
            title = new_title,
            content = new_content,
            isAnon = false,
            commentOn = false,
        )

        val editNormalPostRequestString = objectMapper.writeValueAsString(editNormalPostRequestDto)

        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.put(finalUrl, post.id!!)
                .contentType(MediaType.APPLICATION_JSON)
                .content(editNormalPostRequestString)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success")))
            .andDo(
                document(
                    "normal-post-edit",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("인증을 위한 Access 토큰 글을 쓰는 유저를 식별하기 위해서 반드시 필요함"),
                    ),
                    requestFields(
                        fieldWithPath("title").description("제목"),
                        fieldWithPath("content").description("글 내용"),
                        fieldWithPath("isAnon").description("익명 여부"),
                        fieldWithPath("commentOn").description("댓글 여부"),
                    ),
                    responseFields(
                        fieldWithPath("data.id").description("게시글 번호"),
                        fieldWithPath("status").description("성공 여부"),
                    ),
                ),
            )
        // then

        val basePosts = normalPostRepository.findAll()

        basePosts.size shouldBe 1
        basePosts[0].title shouldBe "new_title_test"
        basePosts[0].content shouldBe "new_content_test"
    }
}
