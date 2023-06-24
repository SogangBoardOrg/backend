package com.kotlin.boardproject.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlin.boardproject.auth.AuthToken
import com.kotlin.boardproject.auth.AuthTokenProvider
import com.kotlin.boardproject.auth.ProviderType
import com.kotlin.boardproject.common.enums.BlackReason
import com.kotlin.boardproject.common.enums.NormalType
import com.kotlin.boardproject.common.enums.PostStatus
import com.kotlin.boardproject.common.enums.Role
import com.kotlin.boardproject.common.util.log
import com.kotlin.boardproject.dto.comment.BlackCommentRequestDto
import com.kotlin.boardproject.dto.comment.CreateCommentRequestDto
import com.kotlin.boardproject.dto.comment.UpdateCommentRequestDto
import com.kotlin.boardproject.model.*
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
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
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
@ActiveProfiles("test")
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
    private lateinit var blackCommentRepository: BlackCommentRepository

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
                photoList = emptyList(),
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

        val createCommentRequestDto = CreateCommentRequestDto(
            content = content,
            isAnon = true,
            postId = post.id!!,
        )

        val createCommentRequestDtoString = objectMapper.writeValueAsString(createCommentRequestDto)

        // when
        // 예상 쿼리 1. 유저 찾는 쿼리. 2. 포스트 찾는 쿼리, 3. comment만드는 쿼리
        log.info("create comment start")
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post(finalUrl).contentType(MediaType.APPLICATION_JSON)
                .content(createCommentRequestDtoString)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessTokenComment.token}")
                .accept(MediaType.APPLICATION_JSON),
        )
        log.info("create comment end")
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
                        fieldWithPath("content").description("글 내용"),
                        fieldWithPath("isAnon").description("익명 여부"),
                        fieldWithPath("postId").description("글 번호"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("성공 여부"),
                        fieldWithPath("data.id").description("댓글 번호"),
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
    fun 대댓글_추가() {
        val urlPoint = "/{parentId}"
        val finalUrl = "$statsEndPoint$urlPoint"

        val ancestorContent = "ancestor_comment_test"
        val parentContent = "parent_comment_test"
        // log.info(post.id!!.toString())

        // 선조 댓글 생성
        val ancestorComment = Comment(
            content = ancestorContent,
            isAnon = true,
            post = post,
            writer = commentWriter,
        )

        commentRepository.saveAndFlush(ancestorComment)
        // 선조 댓글 생성

        val createParentCommentDto = CreateCommentRequestDto(
            parentContent,
            isAnon = true,
            postId = post.id!!,
        )

        val createParentCommentDtoString = objectMapper.writeValueAsString(createParentCommentDto)

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post(finalUrl, ancestorComment.id!!)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createParentCommentDtoString)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessTokenComment.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "add-single-parent-comment",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("인증을 위한 Access 토큰, 댓글을 쓰는 유저를 위해 필요함"),
                    ),
                    PayloadDocumentation.requestFields(
                        fieldWithPath("content").description("글 내용"),
                        fieldWithPath("isAnon").description("익명 여부"),
                        fieldWithPath("postId").description("글 번호"),
                    ),
                    responseFields(
                        fieldWithPath("data.id").description("댓글 번호"),
                        fieldWithPath("status").description("성공 여부"),
                    ),
                ),
            )

        val commentList = commentRepository.findAll()

        commentList.size shouldBe 2
        commentList[1].content shouldBe "parent_comment_test"
        commentList[1].post shouldBe post
        commentList[1].ancestor shouldBe ancestorComment
        commentList[1].parent shouldBe ancestorComment
    }

    @Test
    @Rollback(true)
    fun 대댓글의_댓글_추가() {
        val urlPoint = "/{parentId}"
        val finalUrl = "$statsEndPoint$urlPoint"

        val ancestorContent = "ancestor_comment_test"
        val parentContent = "parent_comment_test"
        val underContent = "under_comment_test"
        // log.info(post.id!!.toString())

        // 선조 댓글 생성
        val ancestorComment = Comment(
            content = ancestorContent,
            isAnon = true,
            post = post,
            writer = commentWriter,
        )

        commentRepository.saveAndFlush(ancestorComment)
        // 선조 댓글 생성

        val parentComment = Comment(
            content = parentContent,
            isAnon = true,
            post = post,
            writer = postWriter,
            ancestor = ancestorComment,
            parent = ancestorComment,
        )

        commentRepository.saveAndFlush(parentComment)

        val createUnderCommentDto = CreateCommentRequestDto(
            underContent,
            isAnon = true,
            postId = post.id!!,
        )

        val createParentCommentDtoString = objectMapper.writeValueAsString(createUnderCommentDto)

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post(finalUrl, parentComment.id!!)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createParentCommentDtoString)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessTokenComment.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "add-single-descendent-comment",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("인증을 위한 Access 토큰, 댓글을 쓰는 유저를 위해 필요함"),
                    ),
                    PayloadDocumentation.requestFields(
                        fieldWithPath("content").description("글 내용"),
                        fieldWithPath("isAnon").description("익명 여부"),
                        fieldWithPath("postId").description("글 번호"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("성공 여부"),
                        fieldWithPath("data.id").description("댓글 번호"),
                    ),
                ),
            )

        val commentList = commentRepository.findAll()

        commentList.size shouldBe 3
        commentList[2].content shouldBe underContent
        commentList[2].post shouldBe post
        commentList[2].ancestor shouldBe ancestorComment
        commentList[2].parent shouldBe parentComment
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
                    responseFields(
                        fieldWithPath("status").description("성공 여부"),
                        fieldWithPath("data.id").description("댓글 번호"),
                    ),
                ),
            )

        val commentList = commentRepository.findAll()

        commentList.size shouldBe 1
        commentList[0].status shouldBe PostStatus.DELETED
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
                        fieldWithPath("content").description("수정할 댓글 내용"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("성공 여부"),
                        fieldWithPath("data.id").description("댓글 번호"),
                        fieldWithPath("data.content").description("수정된 댓글 내용"),
                    ),
                ),
            )

        val commentList = commentRepository.findAll()

        commentList.size shouldBe 1
        commentList[0].status shouldBe PostStatus.NORMAL
        commentList[0].content shouldBe updateComment
    }

    @Test
    @Rollback(true)
    fun 댓글_추천() {
        // given
        val urlPoint = "/like/{commentId}"
        val finalUrl = "$statsEndPoint$urlPoint"

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
                    responseFields(
                        fieldWithPath("data.id").description("댓글 번호"),
                        fieldWithPath("status").description("성공 여부"),
                    ),
                ),
            )

        // then
        val likes = likeCommentRepository.findAll()

        likes.size shouldBe 1
        likes[0].user shouldBe commentWriter
        likes[0].comment shouldBe comment
    }

    @Test
    @Rollback(true)
    fun 댓글_추천_취소() {
        // given
        val urlPoint = "/like/{postId}"
        val finalUrl = "$statsEndPoint$urlPoint"

        val content = "content_test"

        val comment = Comment(
            content = content,
            isAnon = true,
            post = post,
            writer = commentWriter,
        )

        commentRepository.saveAndFlush(comment)

        likeCommentRepository.save(
            LikeComment(
                user = postWriter,
                comment = comment,
            ),
        )

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.delete(finalUrl, comment.id!!).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessTokenPost.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "like-comment-cancel",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("인증을 위한 Access 토큰, 추천을 취소하는 유저를 식별하기 위해서 반드시 필요함"),
                    ),
                    responseFields(
                        fieldWithPath("data.id").description("게시글 번호"),
                        fieldWithPath("status").description("성공 여부"),
                    ),
                ),
            )

        // then
        val likes = likeCommentRepository.findAll()

        likes.size shouldBe 0
    }

    @Test
    @Rollback(true)
    fun 댓글_신고() {
        // TODO: 테스트 추가
        val urlPoint = "/black/{commentId}"
        val finalUrl = "$statsEndPoint$urlPoint"

        val content = "content_test"

        val comment = Comment(
            content = content,
            isAnon = true,
            post = post,
            writer = commentWriter,
        )

        commentRepository.saveAndFlush(comment)

        val blackCommentRequestDto = BlackCommentRequestDto(
            blackReason = BlackReason.HATE,
        )
        val blackCommentRequestDtoString = objectMapper.writeValueAsString(blackCommentRequestDto)

        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post(finalUrl, comment.id!!).contentType(MediaType.APPLICATION_JSON)
                .content(blackCommentRequestDtoString)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessTokenPost.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                MockMvcRestDocumentation.document(
                    "comment-black",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("인증을 위한 Access 토큰, 댓글을 신고하는 유저를 식별하기 위해서 반드시 필요함"),
                    ),
                    PayloadDocumentation.requestFields(
                        fieldWithPath("blackReason").description("신고 사유"),
                    ),
                    responseFields(
                        fieldWithPath("data.id").description("댓글 번호"),
                        fieldWithPath("status").description("성공 여부"),
                    ),
                ),
            )
        // then

        val blackComment = blackCommentRepository.findAll()

        blackComment.size shouldBe 1
        blackComment[0].comment shouldBe comment
        blackComment[0].comment.post shouldBe post
        blackComment[0].user shouldBe postWriter
        blackComment[0].blackReason shouldBe BlackReason.HATE
    }

    @Test
    @Rollback(true)
    fun 작성_댓글_조회() {
        // TODO: 테스트 추가 여기는 mydata init하자...
        val urlPoint = "/mycomment"
        val finalUrl = "$statsEndPoint$urlPoint"

        val content = "comment_test"

        // log.info(post.id!!.toString())

        commentRepository.saveAndFlush(
            Comment(
                content = content,
                isAnon = true,
                post = post,
                writer = commentWriter,
            ),
        )

        commentRepository.saveAndFlush(
            Comment(
                content = content,
                isAnon = true,
                post = post,
                writer = commentWriter,
            ),
        )

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
                    "view-my-comment",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("인증을 위한 Access 토큰, 자신이 쓴 댓글을 조회하는 유저를 위해 필요함"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("성공 여부"),
                        fieldWithPath("data.contents.[].id").type(JsonFieldType.NUMBER).description("댓글 번호"),
                        fieldWithPath("data.contents.[].postId").type(JsonFieldType.NUMBER)
                            .description("댓글이 달려있는 글의 번호"),
                        fieldWithPath("data.contents.[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("data.contents.[].createdTime").type(JsonFieldType.STRING).description("글 생성시간"),
                        fieldWithPath("data.contents.[].lastModifiedTime").type(JsonFieldType.STRING)
                            .description("글 수정시간"),
                        fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지"),
                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 글의 개수"),
                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("페이지의 개수"),
                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 당 나타내는 원소의 개수"),
                    ),
                ),
            )

        val commentList = commentRepository.findAll()

        commentList.size shouldBe 2
    }
}
