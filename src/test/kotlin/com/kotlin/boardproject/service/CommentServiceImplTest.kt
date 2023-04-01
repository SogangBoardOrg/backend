package com.kotlin.boardproject.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlin.boardproject.auth.AuthToken
import com.kotlin.boardproject.auth.AuthTokenProvider
import com.kotlin.boardproject.auth.ProviderType
import com.kotlin.boardproject.common.enums.NormalType
import com.kotlin.boardproject.common.enums.PostStautus
import com.kotlin.boardproject.common.enums.Role
import com.kotlin.boardproject.dto.comment.CreateCommentRequestDto
import com.kotlin.boardproject.dto.comment.UpdateCommentRequestDto
import com.kotlin.boardproject.model.BasePost
import com.kotlin.boardproject.model.Comment
import com.kotlin.boardproject.model.NormalPost
import com.kotlin.boardproject.model.User
import com.kotlin.boardproject.repository.*
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
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*
import javax.transaction.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
@ActiveProfiles("local")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommentServiceImplTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var normalPostRepository: NormalPostRepository

    @Autowired
    private lateinit var commentRepository: CommentRepository

    @Autowired
    private lateinit var blackPostRepository: BlackPostRepository

    @Autowired
    private lateinit var likeCommentRepository: LikeCommentRepository

    @Autowired
    private lateinit var scrapPostRepository: ScrapPostRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var tokenProvider: AuthTokenProvider

    private lateinit var post: BasePost

    private lateinit var postWriter: User

    private lateinit var commentWriter: User

    private lateinit var accessTokenPost: AuthToken

    private lateinit var accessTokenComment: AuthToken

    val statsEndPoint = "/api/v1/comment"

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
            ),
        )
    }

    @Test
    @Rollback(true)
    fun 댓글_생성() {
        // given
        val urlPoint = ""
        val finalUrl = "$statsEndPoint$urlPoint"

        val content = "comment_test"

        // log.info(post.id!!.toString())

        val createCommentRequestDto = CreateCommentRequestDto(
            content = content,
            isAnon = true,
            postId = post.id!!,
        )

        val createCommentRequestDtoString = objectMapper.writeValueAsString(createCommentRequestDto)

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post(finalUrl).contentType(MediaType.APPLICATION_JSON)
                .content(createCommentRequestDtoString)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessTokenComment.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "add-single-comment",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("인증을 위한 Access 토큰, 댓글을 쓰는 유저를 위해 필요함"),
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("content").description("글 내용"),
                        PayloadDocumentation.fieldWithPath("isAnon").description("익명 여부"),
                        PayloadDocumentation.fieldWithPath("postId").description("글 번호"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("status").description("성공 여부"),
                        PayloadDocumentation.fieldWithPath("data.id").description("댓글 번호"),
                    ),
                ),
            )

        val commentList = commentRepository.findAll()

        commentList.size shouldBe 1
        commentList[0].content shouldBe "comment_test"
        commentList[0].post shouldBe post
    }

    @Test
    @Rollback(true)
    fun 댓글_삭제() {
        // given
        val urlPoint = "/{commentId}"
        val finalUrl = "$statsEndPoint$urlPoint"

        val content = "comment_test"

        // log.info(post.id!!.toString())

        val comment = Comment(
            content = content,
            isAnon = true,
            post = post,
            writer = commentWriter,
        )

        commentRepository.saveAndFlush(comment)

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.delete(finalUrl, comment.id!!).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessTokenComment.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "delete-single-comment",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("인증을 위한 Access 토큰, 삭제하는 유저를 식별하기 위해 필요함"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("status").description("성공 여부"),
                        PayloadDocumentation.fieldWithPath("data.id").description("댓글 번호"),
                    ),
                ),
            )

        val commentList = commentRepository.findAll()

        commentList.size shouldBe 1
        commentList[0].status shouldBe PostStautus.DELETED
        commentList[0].post shouldBe post
    }

    @Test
    @Rollback(true)
    fun 댓글_수정() {
        // given
        val urlPoint = "/{commentId}"
        val finalUrl = "$statsEndPoint$urlPoint"

        val content = "comment_test"
        val updateComment = "edit_comment"

        // log.info(post.id!!.toString())

        val comment = Comment(
            content = content,
            isAnon = true,
            post = post,
            writer = commentWriter,
        )

        commentRepository.saveAndFlush(comment)

        val updateCommentRequestDto = UpdateCommentRequestDto(
            updateComment,
        )

        val updateCommentRequestDtoString = objectMapper.writeValueAsString(updateCommentRequestDto)

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.put(finalUrl, comment.id!!).contentType(MediaType.APPLICATION_JSON)
                .content(updateCommentRequestDtoString)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessTokenComment.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "update-single-comment",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("인증을 위한 Access 토큰, 삭제하는 유저를 식별하기 위해 필요함"),
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("content").description("수정할 댓글 내용"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("status").description("성공 여부"),
                        PayloadDocumentation.fieldWithPath("data.id").description("댓글 번호"),
                        PayloadDocumentation.fieldWithPath("data.content").description("수정된 댓글 내용"),
                    ),
                ),
            )

        val commentList = commentRepository.findAll()

        commentList.size shouldBe 1
        commentList[0].status shouldBe PostStautus.NORMAL
        commentList[0].content shouldBe updateComment
    }

    @Test
    @Rollback(true)
    fun 댓글_추천() {
        // given
        val urlPoint = "/like/{commentId}"
        val finalUrl = "$statsEndPoint$urlPoint"

        val title = "title_test"
        val content = "content_test"

        val comment = Comment(
            content = content,
            isAnon = true,
            post = post,
            writer = commentWriter,
        )

        commentRepository.saveAndFlush(comment)
        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post(finalUrl, comment.id!!).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessTokenComment.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "like-comment-add",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("인증을 위한 Access 토큰, 추천을 하는 유저를 식별하기 위해서 반드시 필요함"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("data.id").description("댓글 번호"),
                        PayloadDocumentation.fieldWithPath("status").description("성공 여부"),
                    ),
                ),
            )

        // then
        val likes = likeCommentRepository.findAll()

        likes.size shouldBe 1
        likes[0].user shouldBe commentWriter
        likes[0].comment shouldBe comment
    }
}
