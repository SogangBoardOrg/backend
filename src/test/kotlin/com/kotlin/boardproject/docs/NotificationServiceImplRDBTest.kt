package com.kotlin.boardproject.docs

import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlin.boardproject.domain.comment.dto.create.CreateCommentRequestDto
import com.kotlin.boardproject.domain.comment.repository.CommentRepository
import com.kotlin.boardproject.domain.notification.domain.Notification
import com.kotlin.boardproject.domain.notification.repository.NotificationRepository
import com.kotlin.boardproject.domain.post.domain.BasePost
import com.kotlin.boardproject.domain.post.repository.BasePostRepository
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.global.enums.PostType
import com.kotlin.boardproject.global.enums.ProviderType
import com.kotlin.boardproject.global.enums.Role
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
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.Date
import java.util.UUID
import javax.transaction.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NotificationServiceImplRDBTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var notificationRepository: NotificationRepository

    @Autowired
    private lateinit var commentRepository: CommentRepository

    @Autowired
    private lateinit var basePostRepository: BasePostRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var tokenProvider: AuthTokenProvider

    private lateinit var post: BasePost

    private lateinit var postWriter: User

    private lateinit var commentWriter: User

    private lateinit var accessTokenPost: AuthToken

    private lateinit var accessTokenComment: AuthToken

    private lateinit var notification: Notification

    val statsEndPoint = "/api/v1/notifications"

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
        postWriter = userRepository.saveAndFlush(user)

        accessTokenPost = tokenProvider.createAuthToken(
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
        commentWriter = userRepository.saveAndFlush(userTwo)

        accessTokenComment = tokenProvider.createAuthToken(
            email = "test2@test.com",
            expiry = Date(Date().time + 6000000),
            role = Role.ROLE_VERIFIED_USER.code,
        )

        post = basePostRepository.saveAndFlush(
            BasePost(
                title = "title",
                content = "content",
                isAnon = true,
                commentOn = true,
                writer = postWriter,
                postType = PostType.FREE,
                photoList = emptyList(),
            ),
        )

        notification = notificationRepository.saveAndFlush(
            Notification(
                from = commentWriter,
                to = postWriter,
                url = "/post/${post.id}",
                content = "message",
            ),
        )
    }

    @Test
    fun 알림_받기() {
        val urlPoint = ""
        val finalUrl = "$statsEndPoint$urlPoint"

        val content = "comment_test"

        val createCommentRequestDto = CreateCommentRequestDto(
            content = content,
            isAnon = true,
            postId = post.id!!,
        )

        val createCommentRequestDtoString = objectMapper.writeValueAsString(createCommentRequestDto)

        // when
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/comment").contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessTokenComment.token}")
                .content(createCommentRequestDtoString)
                .accept(MediaType.APPLICATION_JSON),
        )

        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.get(finalUrl).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessTokenPost.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "get-all-notification",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("인증을 위한 Access 토큰, 알림을 받을 사용자의 이름을 해더에 넣어줘야 한다."),
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                        fieldWithPath("data.notificationCount").type(JsonFieldType.NUMBER).description("읽지 않은 알림의 수"),
                        fieldWithPath("data.notifications").type(JsonFieldType.ARRAY).description("읽지 않은 알림의 모임"),
                        // NotificationResponseDto 내부 필드
                        fieldWithPath("data.notifications[].id").type(JsonFieldType.NUMBER).description("알림 ID"),
                        fieldWithPath("data.notifications[].from").type(JsonFieldType.STRING).description("알림 발신자"),
                        fieldWithPath("data.notifications[].url").type(JsonFieldType.STRING).description("알림과 관련된 URL"),
                        fieldWithPath("data.notifications[].content").type(JsonFieldType.STRING).description("알림 내용"),
                        fieldWithPath("data.notifications[].notificationType").type(JsonFieldType.STRING)
                            .description("알림 타입"),
                        fieldWithPath("data.notifications[].createdAt").type(JsonFieldType.STRING)
                            .description("알림 생성 시간"),
                    ),
                ),
            )

        val notificationList = notificationRepository.findAll()

        notificationList.size shouldBe 2
    }

    @Test
    fun 알림을_만들기() {
        val urlPoint = ""
        val finalUrl = "/api/v1/comment"

        val content = "comment_test"

        val createCommentRequestDto = CreateCommentRequestDto(
            content = content,
            isAnon = true,
            postId = post.id!!,
        )

        val createCommentRequestDtoString = objectMapper.writeValueAsString(createCommentRequestDto)

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post(finalUrl).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessTokenComment.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        // then

        val notificationList = notificationRepository.findAll()

        notificationList.size shouldBe 1
    }

    @Test
    fun 알림을_이메일과_알림ID로_읽기() {
        val urlPoint = "/{notificationId}"
        val finalUrl = "$statsEndPoint$urlPoint"
        val notificationId = notification.id!!

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.delete(finalUrl, notificationId).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessTokenPost.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "read-one-notification",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("인증을 위한 Access 토큰, 알림을 읽을 사용자의 이름을 해더에 넣어줘야 한다."),
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                        fieldWithPath("data").type(JsonFieldType.STRING).description("성공여부"),
                    ),
                ),
            )

        val notificationList = notificationRepository.findAll()

        notificationList[0].isRead shouldBe true
    }

    @Test
    fun 알림을_이메일로_읽기() {
        val urlPoint = ""
        val finalUrl = "$statsEndPoint$urlPoint"

        notificationRepository.saveAndFlush(
            Notification(
                from = commentWriter,
                to = postWriter,
                url = "/post/${post.id}",
                content = "message",
            ),
        )

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.delete(finalUrl).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessTokenPost.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "read-all-notification",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("인증을 위한 Access 토큰, 알림을 읽을 사용자의 이름을 해더에 넣어줘야 한다."),
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                        fieldWithPath("data").type(JsonFieldType.STRING).description("성공여부"),
                    ),
                ),
            )

        val notificationList = notificationRepository.findAll()

        notificationList.size shouldBe 2

        notificationList[0].isRead shouldBe true
        notificationList[1].isRead shouldBe true
    }
}
