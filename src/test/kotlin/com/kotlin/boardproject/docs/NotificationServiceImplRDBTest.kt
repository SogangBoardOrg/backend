package com.kotlin.boardproject.docs

import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlin.boardproject.domain.comment.dto.CreateCommentRequestDto
import com.kotlin.boardproject.domain.comment.repository.CommentRepository
import com.kotlin.boardproject.domain.notification.domain.Notification
import com.kotlin.boardproject.domain.notification.repository.NotificationRepository
import com.kotlin.boardproject.domain.post.domain.BasePost
import com.kotlin.boardproject.domain.post.domain.NormalPost
import com.kotlin.boardproject.domain.post.repository.NormalPostRepository
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.global.enums.NormalType
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
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.PayloadDocumentation
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
    private lateinit var normalPostRepository: NormalPostRepository

    @Autowired
    private lateinit var notificationRepository: NotificationRepository

    @Autowired
    private lateinit var commentRepository: CommentRepository

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

        post = normalPostRepository.saveAndFlush(
            NormalPost(
                title = "title",
                content = "content",
                isAnon = true,
                commentOn = true,
                writer = postWriter,
                normalType = NormalType.FREE,
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
    fun getNotifications() {
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
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.get(finalUrl).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessTokenComment.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "get-all-notification",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("인증을 위한 Access 토큰, 알림을 받을 사용자의 이름을 해더에 넣어줘야 한다."),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("status").description("성공 여부"),
                        PayloadDocumentation.fieldWithPath("data.notificationCount").description("읽지 않은 알림의 수"),
                        PayloadDocumentation.fieldWithPath("data.notifications").description("읽지 않은 알림의 모임"),
                    ),
                ),
            )

        val notificationList = notificationRepository.findAll()

        notificationList.size shouldBe 1
    }

    @Test
    fun createNotification() {
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
    fun deleteNotificationByEmailAndNotificationId() {
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
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("인증을 위한 Access 토큰, 알림을 읽을 사용자의 이름을 해더에 넣어줘야 한다."),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("status").description("성공 여부"),
                        PayloadDocumentation.fieldWithPath("data").description("성공여부"),
                    ),
                ),
            )

        val notificationList = notificationRepository.findAll()

        notificationList[0].isRead shouldBe true
    }

    @Test
    fun deleteAllUnreadNotificationByEmail() {
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
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("인증을 위한 Access 토큰, 알림을 읽을 사용자의 이름을 해더에 넣어줘야 한다."),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("status").description("성공 여부"),
                        PayloadDocumentation.fieldWithPath("data").description("성공여부"),
                    ),
                ),
            )

        val notificationList = notificationRepository.findAll()

        notificationList.size shouldBe 2

        notificationList[0].isRead shouldBe true
        notificationList[1].isRead shouldBe true
    }
}
