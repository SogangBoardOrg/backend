package com.kotlin.boardproject.docs

import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlin.boardproject.domain.comment.domain.Comment
import com.kotlin.boardproject.domain.comment.repository.CommentRepository
import com.kotlin.boardproject.domain.post.domain.BasePost
import com.kotlin.boardproject.domain.post.domain.LikePost
import com.kotlin.boardproject.domain.post.domain.ScrapPost
import com.kotlin.boardproject.domain.post.dto.black.BlackPostRequestDto
import com.kotlin.boardproject.domain.post.dto.create.CreatePostRequestDto
import com.kotlin.boardproject.domain.post.dto.edit.EditPostRequestDto
import com.kotlin.boardproject.domain.post.repository.BasePostRepository
import com.kotlin.boardproject.domain.post.repository.BlackPostRepository
import com.kotlin.boardproject.domain.post.repository.LikePostRepository
import com.kotlin.boardproject.domain.post.repository.ScrapPostRepository
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.global.enums.BlackReason
import com.kotlin.boardproject.global.enums.PostType
import com.kotlin.boardproject.global.enums.ProviderType
import com.kotlin.boardproject.global.enums.Role
import com.kotlin.boardproject.global.util.AuthToken
import com.kotlin.boardproject.global.util.AuthTokenProvider
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
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParameters
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostServiceImplTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var commentRepository: CommentRepository

    @Autowired
    private lateinit var basePostRepository: BasePostRepository

    @Autowired
    private lateinit var blackPostRepository: BlackPostRepository

    @Autowired
    private lateinit var likePostRepository: LikePostRepository

    @Autowired
    private lateinit var scrapPostRepository: ScrapPostRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var tokenProvider: AuthTokenProvider

    private lateinit var writer: User

    private lateinit var user2: User

    private lateinit var accessToken: AuthToken

    private lateinit var accessToken2: AuthToken

    val statsEndPoint = "/api/v1/post"

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
        writer = userRepository.saveAndFlush(user)

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

    @AfterEach
    fun cleardb() {
    }

    @Test
    @Rollback(true)
    fun 일반게시판_글_정상_등록() {
        // given
        val urlPoint = ""
        val finalUrl = "$statsEndPoint$urlPoint"

        val title = "title_test"
        val content = "content_test"

        val createPostRequestDto = CreatePostRequestDto(
            title = title,
            content = content,
            isAnon = true,
            commentOn = true,
            postType = PostType.FREE,
            photoList = listOf(),
        )

        val postString = objectMapper.writeValueAsString(createPostRequestDto)
        // when

        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post(finalUrl).contentType(MediaType.APPLICATION_JSON)
                .content(postString).header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                document(
                    "free-post-create",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description(
                            "인증을 위한 Access 토큰, 글을 쓰는 유저를 식별하기 위해서 반드시 필요함",
                        ),
                    ),
                    requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("글 내용"),
                        fieldWithPath("isAnon").type(JsonFieldType.BOOLEAN).description("익명 여부"),
                        fieldWithPath("commentOn").type(JsonFieldType.BOOLEAN).description("댓글 여부"),
                        fieldWithPath("postType").type(JsonFieldType.STRING).description("게시글 타입"),
                        fieldWithPath("reviewScore").type(JsonFieldType.NUMBER).optional().description("리뷰 평점"),
                        fieldWithPath("courseId").type(JsonFieldType.NUMBER).optional().description("코스 번호"),
                        fieldWithPath("photoList").type(JsonFieldType.ARRAY).description("사진 URL 배열"),
                    ),
                    responseFields(
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("게시글 ID"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                    ),
                ),
            )
        // then

        val basePosts = basePostRepository.findAll()

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

        val post = basePostRepository.saveAndFlush(
            BasePost(
                title = title,
                content = content,
                isAnon = true,
                commentOn = true,
                writer = writer,
                postType = PostType.FREE,
                photoList = listOf(),
            ),
        )

        val editPostRequestDto = EditPostRequestDto(
            title = new_title,
            content = new_content,
            isAnon = false,
            commentOn = false,
            photoList = listOf(),
        )

        val editPostRequestString = objectMapper.writeValueAsString(editPostRequestDto)

        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.put(finalUrl, post.id!!).contentType(MediaType.APPLICATION_JSON)
                .content(editPostRequestString).header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                document(
                    "free-post-edit",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description(
                            "인증을 위한 Access 토큰, 글을 수정하는 유저를 식별하기 위해서 반드시 필요함",
                        ),
                    ),
                    requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("글 내용"),
                        fieldWithPath("isAnon").type(JsonFieldType.BOOLEAN).description("익명 여부"),
                        fieldWithPath("commentOn").type(JsonFieldType.BOOLEAN).description("댓글 여부"),
                        fieldWithPath("photoList").type(JsonFieldType.ARRAY).description("사진 URL 배열"),
                    ),
                    responseFields(
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("게시글 ID"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                    ),
                ),
            )
        // then

        val basePosts = basePostRepository.findAll()

        basePosts.size shouldBe 1
        basePosts[0].title shouldBe "new_title_test"
        basePosts[0].content shouldBe "new_content_test"
    }

    @Test
    @Rollback(true)
    fun 일반게시판_글_삭제() {
        val urlPoint = "/{postId}"
        val finalUrl = "$statsEndPoint$urlPoint"

        val title = "title_test"
        val content = "content_test"

        val post = basePostRepository.saveAndFlush(
            BasePost(
                title = title,
                content = content,
                isAnon = true,
                commentOn = true,
                writer = writer,
                postType = PostType.FREE,
            ),
        )

        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.delete(finalUrl, post.id!!).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken.token}").accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                document(
                    "free-post-delete",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description(
                            "인증을 위한 Access 토큰, 글을 삭제하는 유저를 식별하기 위해서 반드시 필요함",
                        ),
                    ),
                    responseFields(
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("게시글 번호"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                    ),
                ),
            )
        // then

        val basePosts = basePostRepository.findAll()

        basePosts.size shouldBe 1
        // writer.postList.size shouldBe 0
    }

    @Test
    @Rollback(true)
    fun 게시물_신고() {
        val urlPoint = "/{postId}/black"
        val finalUrl = "$statsEndPoint$urlPoint"

        val title = "title_test"
        val content = "content_test"

        val post = basePostRepository.saveAndFlush(
            BasePost(
                title = title,
                content = content,
                isAnon = true,
                commentOn = true,
                writer = writer,
                postType = PostType.FREE,
            ),
        )

        val blackPostRequestDto = BlackPostRequestDto(
            blackReason = BlackReason.HATE,
        )
        val blackPostRequestDtoString = objectMapper.writeValueAsString(blackPostRequestDto)

        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post(finalUrl, post.id!!).contentType(MediaType.APPLICATION_JSON)
                .content(blackPostRequestDtoString).header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken2.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                document(
                    "post-black",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description(
                            "인증을 위한 Access 토큰, 글을 신고하는 유저를 식별하기 위해서 반드시 필요함",
                        ),
                    ),
                    requestFields(
                        fieldWithPath("blackReason").type(JsonFieldType.STRING).description("신고 사유"),
                    ),
                    responseFields(
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("게시글 번호"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                    ),
                ),
            )
        // then

        val blackPosts = blackPostRepository.findAll()

        blackPosts.size shouldBe 1
        blackPosts[0].post shouldBe post
        blackPosts[0].user shouldBe user2
        blackPosts[0].blackReason shouldBe BlackReason.HATE
    }

    @Test
    @Rollback(true)
    fun 글_추천_등록() {
        // given
        val urlPoint = "/{postId}/like"
        val finalUrl = "$statsEndPoint$urlPoint"

        val title = "title_test"
        val content = "content_test"

        val post = basePostRepository.saveAndFlush(
            BasePost(
                title = title,
                content = content,
                isAnon = true,
                commentOn = true,
                writer = writer,
                postType = PostType.FREE,
            ),
        )

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post(finalUrl, post.id!!).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken2.token}").accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                document(
                    "like-post-add",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description(
                            "인증을 위한 Access 토큰, 추천을 하는 유저를 식별하기 위해서 반드시 필요함",
                        ),
                    ),
                    responseFields(
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("게시글 번호"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                    ),
                ),
            )

        // then
        val likes = likePostRepository.findAll()

        likes.size shouldBe 1
        likes[0].user shouldBe user2
        likes[0].post shouldBe post
    }

    @Test
    @Rollback(true)
    fun 글_추천_취소() {
        // given
        val urlPoint = "/{postId}/like"
        val finalUrl = "$statsEndPoint$urlPoint"

        val title = "title_test"
        val content = "content_test"

        val post = basePostRepository.saveAndFlush(
            BasePost(
                title = title,
                content = content,
                isAnon = true,
                commentOn = true,
                writer = writer,
                postType = PostType.FREE,
            ),
        )

        likePostRepository.save(
            LikePost(
                user = user2,
                post = post,
            ),
        )

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.delete(finalUrl, post.id!!).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken2.token}").accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                document(
                    "like-post-cancel",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description(
                            "인증을 위한 Access 토큰, 추천을 취소하는 유저를 식별하기 위해서 반드시 필요함",
                        ),
                    ),
                    responseFields(
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("게시글 번호"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                    ),
                ),
            )

        // then
        val likes = likePostRepository.findAll()

        likes.size shouldBe 0
    }

    @Test
    @Rollback(true)
    fun 글_스크랩() {
        // given
        val urlPoint = "/{postId}/scrap"
        val finalUrl = "$statsEndPoint$urlPoint"

        val title = "title_test"
        val content = "content_test"

        val post = basePostRepository.saveAndFlush(
            BasePost(
                title = title,
                content = content,
                isAnon = true,
                commentOn = true,
                writer = writer,
                postType = PostType.FREE,
            ),
        )

        val post2 = basePostRepository.saveAndFlush(
            BasePost(
                title = "title_2",
                content = "content_2",
                isAnon = true,
                commentOn = true,
                writer = writer,
                postType = PostType.FREE,
            ),
        )

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.post(finalUrl, post.id!!).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken2.token}").accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                document(
                    "scrap-post-add",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description(
                            "인증을 위한 Access 토큰, 스크랩 하는 유저를 식별하기 위해서 반드시 필요함",
                        ),
                    ),
                    responseFields(
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("게시글 번호"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                    ),
                ),
            )

        // 2번 글도 스크래핑
        mockMvc.perform(
            RestDocumentationRequestBuilders.post(finalUrl, post2.id!!).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken2.token}").accept(MediaType.APPLICATION_JSON),
        )

        // then
        val scrapeList = scrapPostRepository.findAll()

        scrapeList.size shouldBe 2
    }

    @Test
    @Rollback(true)
    fun 글_스크랩_취소() {
        // given
        val urlPoint = "/{postId}/scrap"
        val finalUrl = "$statsEndPoint$urlPoint"

        val title = "title_test"
        val content = "content_test"

        val post = basePostRepository.saveAndFlush(
            BasePost(
                title = title,
                content = content,
                isAnon = true,
                commentOn = true,
                writer = writer,
                postType = PostType.FREE,
            ),
        )

        val post2 = basePostRepository.saveAndFlush(
            BasePost(
                title = "title_2",
                content = "content_2",
                isAnon = true,
                commentOn = true,
                writer = writer,
                postType = PostType.FREE,
            ),
        )

        // when
        mockMvc.perform(
            RestDocumentationRequestBuilders.post(finalUrl, post.id!!).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken2.token}").accept(MediaType.APPLICATION_JSON),
        )

        // 2번 글도 스크래핑
        mockMvc.perform(
            RestDocumentationRequestBuilders.post(finalUrl, post2.id!!).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken2.token}").accept(MediaType.APPLICATION_JSON),
        )

        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.delete(finalUrl, post.id!!).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken2.token}").accept(MediaType.APPLICATION_JSON),
        )

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                document(
                    "scrap-post-cancel",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description(
                            "인증을 위한 Access 토큰, 스크랩 하는 유저를 식별하기 위해서 반드시 필요함",
                        ),
                    ),
                    responseFields(
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("게시글 번호"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                    ),
                ),
            )

        // then
        val scrapeList = scrapPostRepository.findAll()

        scrapeList.size shouldBe 1
    }

    @Test
    @Rollback(true)
    fun 글_단건_조회_회원() {
        // given
        val urlPoint = "/{postId}"
        val finalUrl = "$statsEndPoint$urlPoint"

        val title = "title_test"
        val content = "content_test"

        val post = basePostRepository.saveAndFlush(
            BasePost(
                title = title,
                content = content,
                isAnon = true,
                commentOn = true,
                writer = writer,
                postType = PostType.FREE,
            ),
        )

        val post2 = basePostRepository.saveAndFlush(
            BasePost(
                title = "title_2",
                content = "content_2",
                isAnon = true,
                commentOn = true,
                writer = writer,
                postType = PostType.FREE,
            ),
        )

        val comment = commentRepository.saveAndFlush(
            Comment(
                content = "comment_content",
                writer = writer,
                post = post,
                isAnon = true,
            ),
        )

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.get(finalUrl, post.id!!).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken2.token}").accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("success"))).andDo(
                document(
                    "view-single-free-post-login",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description(
                            "인증을 위한 Access 토큰, 글을 보는 유저를 식별하기 위해서 반드시 필요함",
                        ),
                    ),
                    responseFields(
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("게시글 ID"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("게시글 제목"),
                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("게시글 내용"),
                        fieldWithPath("data.writerName").type(JsonFieldType.STRING).description("게시글 작성자 이름"),
                        fieldWithPath("data.writerProfileImageUrl").type(JsonFieldType.STRING).optional().description(
                            "게시글 작성자 프로필 이미지 url",
                        ),
                        fieldWithPath("data.isAnon").type(JsonFieldType.BOOLEAN).description("게시글 작성자 익명 여부"),
                        fieldWithPath("data.isLiked").type(JsonFieldType.BOOLEAN).optional().description("게시글 좋아요 여부"),
                        fieldWithPath("data.isScrapped").type(JsonFieldType.BOOLEAN).optional()
                            .description("게시글 스크랩 여부"),
                        fieldWithPath("data.isWriter").type(JsonFieldType.BOOLEAN).optional().description("게시글 작성자 여부"),
                        fieldWithPath("data.commentCnt").type(JsonFieldType.NUMBER).description("댓글 숫자"),
                        fieldWithPath("data.commentOn").type(JsonFieldType.BOOLEAN).description("게시글 댓글 작성 가능 여부"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("게시글 작성 시간"),
                        fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("게시글 최종 수정 시간"),
                        fieldWithPath("data.commentList").type(JsonFieldType.ARRAY).description("댓글 내용"),
                        fieldWithPath("data.photoList").type(JsonFieldType.ARRAY).description("사진 URL 배열"),
                        fieldWithPath("data.reviewScore").type(JsonFieldType.NUMBER).optional().description("리뷰 평점"),
                        fieldWithPath("data.postType").type(JsonFieldType.STRING).description("게시글 타입"),
                        // CommentDto 내부 필드
                        fieldWithPath("data.commentList[].id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                        fieldWithPath("data.commentList[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("data.commentList[].isAnon").type(JsonFieldType.BOOLEAN)
                            .description("댓글 작성자 익명 여부"),
                        fieldWithPath("data.commentList[].writerName").type(JsonFieldType.STRING)
                            .description("댓글 작성인 이름"),
                        fieldWithPath("data.commentList[].writerProfileImageUrl").type(JsonFieldType.STRING).optional()
                            .description("댓글 작성인 프로필 이미지 url"),
                        fieldWithPath("data.commentList[].isWriter").type(JsonFieldType.BOOLEAN)
                            .description("댓글이 로그인 한 사용자에 의해 작성됨"),
                        fieldWithPath("data.commentList[].isPostWriter").type(JsonFieldType.BOOLEAN)
                            .description("댓글이 게시글 작성자에 의해 작성됨"),
                        fieldWithPath("data.commentList[].createdAt").type(JsonFieldType.STRING)
                            .description("댓글 생성 시간"),
                        fieldWithPath("data.commentList[].updatedAt").type(JsonFieldType.STRING)
                            .description("댓글 최종 수정 시간"),
                        fieldWithPath("data.commentList[].parentId").type(JsonFieldType.NUMBER).optional()
                            .description("부모 댓글 ID"),
                        fieldWithPath("data.commentList[].ancestorId").type(JsonFieldType.NUMBER).optional()
                            .description("조상 댓글 ID"),
                        fieldWithPath("data.commentList[].isLiked").type(JsonFieldType.BOOLEAN)
                            .description("댓글 좋아요 여부"),
                        fieldWithPath("data.commentList[].likeCnt").type(JsonFieldType.NUMBER).description("댓글 좋아요 개수"),
                        fieldWithPath("data.commentList[].child").type(JsonFieldType.ARRAY).description("대댓글 집합"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                    ),
                ),
            )
    }

    @Test
    @Rollback(true)
    fun 글_단건_조회_비회원() {
        // given
        val urlPoint = "/{postId}"
        val finalUrl = "$statsEndPoint$urlPoint"

        val title = "title_test"
        val content = "content_test"

        val post = basePostRepository.saveAndFlush(
            BasePost(
                title = title,
                content = content,
                isAnon = true,
                commentOn = true,
                writer = writer,
                postType = PostType.FREE,
                photoList = listOf(),
            ),
        )

        val post2 = basePostRepository.saveAndFlush(
            BasePost(
                title = "title_2",
                content = "content_2",
                isAnon = true,
                commentOn = true,
                writer = writer,
                postType = PostType.FREE,
                photoList = listOf(),
            ),
        )

        val comment = commentRepository.saveAndFlush(
            Comment(
                content = "comment_content",
                writer = writer,
                post = post,
                isAnon = true,
            ),
        )

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.get(finalUrl, post.id!!).contentType(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.data.isLiked", false).exists())
            .andExpect(jsonPath("$.data.isScrapped", false).exists())
            .andExpect(jsonPath("$.data.isWriter", false).exists())
            .andDo(
                document(
                    "view-single-free-post-no-login",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("게시글 ID"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("게시글 제목"),
                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("게시글 내용"),
                        fieldWithPath("data.writerName").type(JsonFieldType.STRING).description("게시글 작성자 이름"),
                        fieldWithPath("data.writerProfileImageUrl").type(JsonFieldType.STRING).optional().description(
                            "게시글 작성자 프로필 이미지 url",
                        ),
                        fieldWithPath("data.isAnon").type(JsonFieldType.BOOLEAN).description("게시글 작성자 익명 여부"),
                        fieldWithPath("data.isLiked").type(JsonFieldType.BOOLEAN).optional()
                            .description("게시글 좋아요 여부"),
                        fieldWithPath("data.isScrapped").type(JsonFieldType.BOOLEAN).optional()
                            .description("게시글 스크랩 여부"),
                        fieldWithPath("data.isWriter").type(JsonFieldType.BOOLEAN).optional()
                            .description("게시글 작성자 여부"),
                        fieldWithPath("data.commentCnt").type(JsonFieldType.NUMBER).description("댓글 숫자"),
                        fieldWithPath("data.commentOn").type(JsonFieldType.BOOLEAN).description("게시글 댓글 작성 가능 여부"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("게시글 작성 시간"),
                        fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("게시글 최종 수정 시간"),
                        fieldWithPath("data.commentList").type(JsonFieldType.ARRAY).description("댓글 내용"),
                        fieldWithPath("data.photoList").type(JsonFieldType.ARRAY).description("사진 URL 배열"),
                        fieldWithPath("data.reviewScore").type(JsonFieldType.NUMBER).optional()
                            .description("리뷰 평점"),
                        fieldWithPath("data.postType").type(JsonFieldType.STRING).description("게시글 타입"),
                        // CommentDto 내부 필드
                        fieldWithPath("data.commentList[].id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                        fieldWithPath("data.commentList[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("data.commentList[].isAnon").type(JsonFieldType.BOOLEAN)
                            .description("댓글 작성자 익명 여부"),
                        fieldWithPath("data.commentList[].writerName").type(JsonFieldType.STRING)
                            .description("댓글 작성인 이름"),
                        fieldWithPath("data.commentList[].writerProfileImageUrl").type(JsonFieldType.STRING).optional()
                            .description("댓글 작성인 프로필 이미지 url"),
                        fieldWithPath("data.commentList[].isWriter").type(JsonFieldType.BOOLEAN)
                            .description("댓글이 로그인 한 사용자에 의해 작성됨"),
                        fieldWithPath("data.commentList[].isPostWriter").type(JsonFieldType.BOOLEAN)
                            .description("댓글이 게시글 작성자에 의해 작성됨"),
                        fieldWithPath("data.commentList[].createdAt").type(JsonFieldType.STRING)
                            .description("댓글 생성 시간"),
                        fieldWithPath("data.commentList[].updatedAt").type(JsonFieldType.STRING)
                            .description("댓글 최종 수정 시간"),
                        fieldWithPath("data.commentList[].parentId").type(JsonFieldType.NUMBER).optional()
                            .description("부모 댓글 ID"),
                        fieldWithPath("data.commentList[].ancestorId").type(JsonFieldType.NUMBER).optional()
                            .description("조상 댓글 ID"),
                        fieldWithPath("data.commentList[].isLiked").type(JsonFieldType.BOOLEAN)
                            .description("댓글 좋아요 여부"),
                        fieldWithPath("data.commentList[].likeCnt").type(JsonFieldType.NUMBER)
                            .description("댓글 좋아요 개수"),
                        fieldWithPath("data.commentList[].child").type(JsonFieldType.ARRAY).description("대댓글 집합"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                    ),
                ),
            )
    }

    @Test
    @Rollback(true)
    fun 글_댓글_단독_조회() {
        // given
        val urlPoint = "/{postId}/comments"
        val finalUrl = "$statsEndPoint$urlPoint"

        val title = "title_test"
        val content = "content_test"

        val post = basePostRepository.saveAndFlush(
            BasePost(
                title = title,
                content = content,
                isAnon = true,
                commentOn = true,
                writer = writer,
                postType = PostType.FREE,
                photoList = listOf(),
            ),
        )

        val post2 = basePostRepository.saveAndFlush(
            BasePost(
                title = "title_2",
                content = "content_2",
                isAnon = true,
                commentOn = true,
                writer = writer,
                postType = PostType.FREE,
                photoList = listOf(),
            ),
        )

        val comment = commentRepository.saveAndFlush(
            Comment(
                content = "comment_content",
                writer = writer,
                post = post,
                isAnon = true,
            ),
        )

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.get(finalUrl, post.id!!).contentType(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                document(
                    "view-comments-free-post",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("data.commentList").type(JsonFieldType.ARRAY).description("댓글 내용"),
                        fieldWithPath("data.commentList[].id").type(JsonFieldType.NUMBER).description("댓글 번호"),
                        fieldWithPath("data.commentList[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("data.commentList.[].isAnon").type(JsonFieldType.BOOLEAN)
                            .description("댓글 작성자 익명 여부"),
                        fieldWithPath("data.commentList.[].writerName").type(JsonFieldType.STRING)
                            .description("댓글 작성인 이름"),
                        fieldWithPath("data.commentList.[].writerProfileImageUrl").type(JsonFieldType.STRING).optional()
                            .description("댓글 작성자 프로필 이미지 url"),
                        fieldWithPath("data.commentList.[].isWriter").type(JsonFieldType.BOOLEAN)
                            .description("댓글이 로그인 한 작성자가 작성했는지의 여부"),
                        fieldWithPath("data.commentList.[].isPostWriter").type(JsonFieldType.BOOLEAN)
                            .description("댓글이 글 작성자에 의해서 쓰여졌는지 여부"),
                        fieldWithPath("data.commentList.[].createdAt").type(JsonFieldType.STRING)
                            .description("댓글 생성 시간"),
                        fieldWithPath("data.commentList.[].updatedAt").type(JsonFieldType.STRING)
                            .description("댓글 최종 수정 시간"),
                        fieldWithPath("data.commentList.[].parentId").type(JsonFieldType.NUMBER).optional()
                            .description("부모 댓글 번호"),
                        fieldWithPath("data.commentList.[].ancestorId").type(JsonFieldType.NUMBER).optional()
                            .description("조상 댓글 번호"),
                        fieldWithPath("data.commentList.[].isLiked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                        fieldWithPath("data.commentList.[].likeCnt").type(JsonFieldType.NUMBER).description("좋아요 개수"),
                        fieldWithPath("data.commentList.[].child").type(JsonFieldType.ARRAY).description("대댓글 집합"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                    ),
                ),
            )
    }

    @Test
    @Rollback(true)
    fun 글_대량_조회() {
        // given
        val urlPoint = "/query"
        val finalUrl = "$statsEndPoint$urlPoint"
        val postNumber = 30

        val title = ""
        val content = ""
        val writerName = "test"
        val postType = PostType.FREE
        val page = 0
        val size = 7
        val sort = ""

        // 글 postNumber 만큼 등록
        for (i in 1..postNumber) {
            basePostRepository.saveAndFlush(
                BasePost(
                    title = "title_$i",
                    content = "content_$i",
                    isAnon = false,
                    commentOn = true,
                    writer = writer,
                    postType = PostType.FREE,
                ),
            )
        }

        for (i in 1..postNumber) {
            basePostRepository.saveAndFlush(
                BasePost(
                    title = "diff_$i",
                    content = "diff_$i",
                    isAnon = true,
                    commentOn = true,
                    writer = writer,
                    postType = PostType.FREE,
                ),
            )
        }

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.get(
                "$finalUrl?" +
                    "title=$title&" +
                    "content=$content&" +
                    "writer-name=$writerName&" +
                    "post-type=$postType&" +
                    "page=$page&" +
                    "size=$size&" +
                    "sort=$sort",
            ).contentType(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                document(
                    "view-bulk-free-post-no-login",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestParameters(
                        parameterWithName("title").description("찾을 글 제목").optional(),
                        parameterWithName("content").description("찾을 글의 내용").optional(),
                        parameterWithName("writer-name").description("글 작성자").optional(),
                        parameterWithName("post-type").description("일반 게시판 글 종류"),
                        parameterWithName("course-id").description("찾는 강의 id").optional(),
                        parameterWithName("page").description("찾는 페이지 번호"),
                        parameterWithName("size").description("페이지 당 불러올 글의 크기"),
                        parameterWithName("sort").description("정렬"),
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                        fieldWithPath("data.contents").type(JsonFieldType.ARRAY).description("글 데이터"),
                        fieldWithPath("data.contents.[].id").type(JsonFieldType.NUMBER).description("글 번호"),
                        fieldWithPath("data.contents.[].title").type(JsonFieldType.STRING).description("글 제목"),
                        fieldWithPath("data.contents.[].content").type(JsonFieldType.STRING).description("글 제목"),
                        fieldWithPath("data.contents.[].writerName").type(JsonFieldType.STRING).description("글쓴이"),
                        fieldWithPath("data.contents.[].writerProfileImageUrl").type(JsonFieldType.STRING).optional()
                            .description("글쓴이 프로필 이미지 url"),
                        fieldWithPath("data.contents.[].isAnon").type(JsonFieldType.BOOLEAN).description("익명 여부"),
                        fieldWithPath("data.contents.[].isLiked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                        fieldWithPath("data.contents.[].isScrapped").type(JsonFieldType.BOOLEAN).description("스크랩 여부"),
                        fieldWithPath("data.contents.[].isWriter").type(JsonFieldType.BOOLEAN).description("글쓴이 여부"),
                        fieldWithPath("data.contents.[].postType").type(JsonFieldType.STRING).description("글 종류"),
                        fieldWithPath("data.contents.[].courseId").type(JsonFieldType.NUMBER).description("강의 번호")
                            .optional(),
                        fieldWithPath("data.contents.[].courseYear").type(JsonFieldType.NUMBER).description("강의 년도")
                            .optional(),
                        fieldWithPath("data.contents.[].courseSeason").type(JsonFieldType.STRING).description("강의 계절")
                            .optional(),
                        fieldWithPath("data.contents.[].courseCode").type(JsonFieldType.STRING).description("강의 코드")
                            .optional(),
                        fieldWithPath("data.contents.[].courseName").type(JsonFieldType.STRING).description("강의 이름")
                            .optional(),
                        fieldWithPath("data.contents.[].reviewScore").type(JsonFieldType.NUMBER).description("강의 평점")
                            .optional(),
                        fieldWithPath("data.contents.[].commentOn").type(JsonFieldType.BOOLEAN).description("댓글 여부"),
                        fieldWithPath("data.contents.[].commentCnt").type(JsonFieldType.NUMBER).description("댓글 개수"),
                        fieldWithPath("data.contents.[].likeCnt").type(JsonFieldType.NUMBER).description("좋아요 개수"),
                        fieldWithPath("data.contents.[].scrapCnt").type(JsonFieldType.NUMBER).description("스크랩 개수"),
                        fieldWithPath("data.contents.[].photoCnt").type(JsonFieldType.NUMBER).description("사진 개수"),
                        fieldWithPath("data.contents.[].createdAt").type(JsonFieldType.STRING).description("글 생성시간"),
                        fieldWithPath("data.contents.[].updatedAt").type(JsonFieldType.STRING)
                            .description("글 생성시간"),
                        fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지"),
                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 글의 개수"),
                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("페이지의 개수"),
                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 당 나타내는 원소의 개수"),
                    ),
                ),
            )
    }

    @Test
    @Rollback(true)
    fun 자신이_쓴_글_조회() {
        // given
        val finalUrl = "/api/v1/my/post"
        val postNumber = 30

        val page = 0
        val size = 4
        val sort = ""

        // 글 postNumber 만큼 등록
        for (i in 1..postNumber) {
            basePostRepository.saveAndFlush(
                BasePost(
                    title = "title_$i",
                    content = "content_$i",
                    isAnon = true,
                    commentOn = true,
                    writer = writer,
                    postType = PostType.FREE,
                ),
            )
        }

        for (i in 1..postNumber) {
            basePostRepository.saveAndFlush(
                BasePost(
                    title = "diff_$i",
                    content = "diff_$i",
                    isAnon = true,
                    commentOn = true,
                    writer = writer,
                    postType = PostType.FREE,
                ),
            )
        }

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders
                .get(
                    "$finalUrl?" +
                        "page=$page&" +
                        "size=$size&" +
                        "sort=$sort ",
                )
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                document(
                    "view-my-written-post",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description(
                            "인증을 위한 Access 토큰, 자신의 글을 찾는 유저를 식별하기 위해서 반드시 필요함",
                        ),
                    ),
                    requestParameters(
                        parameterWithName("page").description("찾는 페이지 번호"),
                        parameterWithName("size").description("페이지 당 불러올 글의 크기"),
                        parameterWithName("sort").description("정렬"),
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                        fieldWithPath("data.contents").type(JsonFieldType.ARRAY).description("글 데이터"),
                        fieldWithPath("data.contents.[].id").type(JsonFieldType.NUMBER).description("글 번호"),
                        fieldWithPath("data.contents.[].title").type(JsonFieldType.STRING).description("글 제목"),
                        fieldWithPath("data.contents.[].content").type(JsonFieldType.STRING).description("글 제목"),
                        fieldWithPath("data.contents.[].createdAt").type(JsonFieldType.STRING).description("글 생성시간"),
                        fieldWithPath("data.contents.[].updatedAt").type(JsonFieldType.STRING)
                            .description("글 생성시간"),
                        fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지"),
                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 글의 개수"),
                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("페이지의 개수"),
                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 당 나타내는 원소의 개수"),
                    ),
                ),
            )
    }

    @Test
    @Rollback(true)
    fun 자신이_스크랩_한_글_조회() { // given
        val finalUrl = "/api/v1/my/scrap"

        val page = 0
        val size = 7
        val sort = ""

        // 글 postNumber 만큼 등록

        val post1 = basePostRepository.saveAndFlush(
            BasePost(
                title = "title_1",
                content = "content_1",
                isAnon = true,
                commentOn = true,
                writer = writer,
                postType = PostType.FREE,
            ),
        )

        val post2 = basePostRepository.saveAndFlush(
            BasePost(
                title = "title_2",
                content = "content_2",
                isAnon = true,
                commentOn = true,
                writer = writer,
                postType = PostType.FREE,
            ),
        )

        val post3 = basePostRepository.saveAndFlush(
            BasePost(
                title = "title_3",
                content = "content_3",
                isAnon = true,
                commentOn = true,
                writer = writer,
                postType = PostType.FREE,
            ),
        )

        // 글 1번 스크랩
        scrapPostRepository.saveAndFlush(
            ScrapPost(
                user = user2,
                post = post1,
            ),
        )
        // 글 3번 스크랩
        scrapPostRepository.saveAndFlush(
            ScrapPost(
                user = user2,
                post = post3,
            ),
        )

        // when
        val result = mockMvc.perform(
            RestDocumentationRequestBuilders.get(
                "$finalUrl?" +
                    "page=$page&" +
                    "size=$size&" +
                    "sort=$sort ",
            ).contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken2.token}")
                .accept(MediaType.APPLICATION_JSON),
        )

        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                document(
                    "view-my-scrap-post",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description(
                            "인증을 위한 Access 토큰, 자신이 스크랩한 글을 찾는 유저를 식별하기 위해서 반드시 필요함",
                        ),
                    ),
                    requestParameters(
                        parameterWithName("page").description("찾는 페이지 번호"),
                        parameterWithName("size").description("페이지 당 불러올 글의 크기"),
                        parameterWithName("sort").description("정렬"),
                    ),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("성공 여부"),
                        fieldWithPath("data.contents").type(JsonFieldType.ARRAY).description("글 데이터"),
                        fieldWithPath("data.contents.[].id").type(JsonFieldType.NUMBER).description("글 번호"),
                        fieldWithPath("data.contents.[].title").type(JsonFieldType.STRING).description("글 제목"),
                        fieldWithPath("data.contents.[].content").type(JsonFieldType.STRING).description("글 제목"),
                        fieldWithPath("data.contents.[].createdAt").type(JsonFieldType.STRING).description("글 생성시간"),
                        fieldWithPath("data.contents.[].updatedAt").type(JsonFieldType.STRING)
                            .description("글 생성시간"),
                        fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지"),
                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 글의 개수"),
                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("페이지의 개수"),
                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 당 나타내는 원소의 개수"),
                    ),
                ),
            )
    }
}
