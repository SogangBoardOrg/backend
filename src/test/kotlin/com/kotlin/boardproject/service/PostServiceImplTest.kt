package com.kotlin.boardproject.service

import com.kotlin.boardproject.auth.ProviderType
import com.kotlin.boardproject.common.enums.ErrorCode
import com.kotlin.boardproject.common.enums.NormalType
import com.kotlin.boardproject.common.enums.PostStatus
import com.kotlin.boardproject.common.enums.Role
import com.kotlin.boardproject.common.exception.ConditionConflictException
import com.kotlin.boardproject.common.exception.EntityNotFoundException
import com.kotlin.boardproject.common.exception.UnAuthorizedException
import com.kotlin.boardproject.dto.post.normalpost.CreateNormalPostRequestDto
import com.kotlin.boardproject.dto.post.normalpost.CreateNormalPostResponseDto
import com.kotlin.boardproject.dto.post.normalpost.EditNormalPostRequestDto
import com.kotlin.boardproject.dto.post.normalpost.EditNormalPostResponseDto
import com.kotlin.boardproject.model.LikePost
import com.kotlin.boardproject.model.NormalPost
import com.kotlin.boardproject.model.User
import com.kotlin.boardproject.repository.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.*
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostServiceImplTest {

    private val userOne = User(
        id = UUID.randomUUID(),
        email = "userOne@test.com",
        nickname = "userOne",
        role = Role.ROLE_VERIFIED_USER,
        providerType = ProviderType.LOCAL,
    )

    private val userTwo = User(
        id = UUID.randomUUID(),
        email = "userTwo@test.com",
        nickname = "userTwo",
        role = Role.ROLE_VERIFIED_USER,
        providerType = ProviderType.LOCAL,
    )

    private val userThree = User(
        id = UUID.randomUUID(),
        email = "userThree@test.com",
        nickname = "userThree",
        role = Role.ROLE_VERIFIED_USER,
        providerType = ProviderType.LOCAL,
    )

    private val normalPostOne = NormalPost(
        title = "postOne",
        content = "postOne",
        writer = userOne,
        isAnon = false,
        commentOn = true,
        normalType = NormalType.FREE,
    )

    private val userRepository: UserRepository = mockk()
    private val normalPostRepository: NormalPostRepository = mockk()
    private val basePostRepository: BasePostRepository = mockk()
    private val blackPostRepository: BlackPostRepository = mockk()
    private val likePostRepository: LikePostRepository = mockk()
    private val scrapPostRepository: ScrapPostRepository = mockk()
    private val commentRepository: CommentRepository = mockk()

    private lateinit var postService: PostService

    fun setUserRepository() {
        every { userRepository.findByEmail(userOne.email) } returns userOne
        every { userRepository.findByEmail(userTwo.email) } returns userTwo
        every { userRepository.findByEmail(userThree.email) } returns userThree

        every { userRepository.findByEmailFetchLikeList(userOne.email) } returns userOne
        every { userRepository.findByEmailFetchLikeList(userTwo.email) } returns userTwo
        every { userRepository.findByEmailFetchLikeList(userThree.email) } returns userThree
    }

    @BeforeEach
    fun default_setting() {
        setUserRepository()
        normalPostOne.id = 1L
        postService = PostServiceImpl(
            normalPostRepository,
            userRepository,
            basePostRepository,
            blackPostRepository,
            likePostRepository,
            scrapPostRepository,
            commentRepository,
        )
    }

    @AfterEach
    fun afterTest() {
        clearAllMocks()
        normalPostOne.likeList.clear()

        userOne.likePostList.clear()
        userTwo.likePostList.clear()
        userThree.likePostList.clear()


    }

    @Test
    @DisplayName("일반게시판_글_정상_등록")
    fun createPost() {
        // given
        val createNormalPostRequestDto = CreateNormalPostRequestDto(
            title = "postOne",
            content = "postOne",
            isAnon = false,
            commentOn = true,
            normalType = NormalType.FREE,
            photoList = listOf(),
        )
        val postId = 1L
        every { normalPostRepository.save(any()).id!! } returns postId

        // when
        val data = postService.createNormalPost(userOne.email, createNormalPostRequestDto)

        // then
        verify(exactly = 1) { normalPostRepository.save(any()) }
        data shouldBe CreateNormalPostResponseDto(postId)
    }

    @Test
    @DisplayName("일반게시판_글_수정 - 정상")
    fun editPostNormal() {
        // given
        val editNormalPostRequestDto = EditNormalPostRequestDto(
            title = "postOneEdit",
            content = "postOneEdit",
            isAnon = false,
            commentOn = true,
            photoList = listOf(),
        )
        every { normalPostRepository.findByIdAndStatus(normalPostOne.id!!, PostStatus.NORMAL) } returns normalPostOne

        // when
        val data = postService.editNormalPost(userOne.email, normalPostOne.id!!, editNormalPostRequestDto)

        // then
        verify(exactly = 1) { userRepository.findByEmail(userOne.email) }
        verify(exactly = 1) { normalPostRepository.findByIdAndStatus(normalPostOne.id!!, PostStatus.NORMAL) }
        data shouldBe EditNormalPostResponseDto(normalPostOne.id!!)
    }

    @Test
    @DisplayName("일반게시판_글_수정 - 없는 번호의 글 찾음")
    fun editPostErrorOne() {
        // given
        val editNormalPostRequestDto = EditNormalPostRequestDto(
            title = "postOneEdit",
            content = "postOneEdit",
            isAnon = false,
            commentOn = true,
            photoList = listOf(),
        )
        val postId = 2L

        every { normalPostRepository.findByIdAndStatus(postId, PostStatus.NORMAL) } returns null

        // when
        val exception = shouldThrow<EntityNotFoundException> {
            postService.editNormalPost(userOne.email, postId, editNormalPostRequestDto)
        }
        // then
        verify(exactly = 1) { userRepository.findByEmail(userOne.email) }
        verify(exactly = 1) { normalPostRepository.findByIdAndStatus(postId, PostStatus.NORMAL) }
        exception.log shouldBe "존재하지 않는 글 입니다."
    }

    @Test
    @DisplayName("일반게시판_글_수정 - 삭제된 글 수정")
    fun editPostErrorTwo() {
        // given
        val editNormalPostRequestDto = EditNormalPostRequestDto(
            title = "postOneEdit",
            content = "postOneEdit",
            isAnon = false,
            commentOn = true,
            photoList = listOf(),
        )
        every { normalPostRepository.findByIdAndStatus(normalPostOne.id!!, PostStatus.NORMAL) } returns null

        // when
        val exception = shouldThrow<EntityNotFoundException> {
            postService.editNormalPost(userOne.email, normalPostOne.id!!, editNormalPostRequestDto)
        }
        // then
        verify(exactly = 1) { userRepository.findByEmail(userOne.email) }
        verify(exactly = 1) { normalPostRepository.findByIdAndStatus(normalPostOne.id!!, PostStatus.NORMAL) }
        exception.log shouldBe "존재하지 않는 글 입니다."
    }

    @Test
    @DisplayName("일반게시판_글_수정 - 없는 유저 찾음")
    fun editPostErrorThree() {
        // given
        val editNormalPostRequestDto = EditNormalPostRequestDto(
            title = "postOneEdit",
            content = "postOneEdit",
            isAnon = false,
            commentOn = true,
            photoList = listOf(),
        )
        val errorUserEmail = "error@test.com"

        every { userRepository.findByEmail(errorUserEmail) } returns null

        // when
        val exception = shouldThrow<EntityNotFoundException> {
            postService.editNormalPost(errorUserEmail, normalPostOne.id!!, editNormalPostRequestDto)
        }
        // then
        verify(exactly = 1) { userRepository.findByEmail(errorUserEmail) }
        verify(exactly = 0) { normalPostRepository.findByIdAndStatus(normalPostOne.id!!, PostStatus.NORMAL) }
        exception.log shouldBe "$errorUserEmail 는 없는 유저 이메일 입니다."
    }

    @Test
    @DisplayName("일반게시판_글_수정 - 글 작성자와 수정자가 다름")
    fun editPostErrorFour() {
        // given
        val editNormalPostRequestDto = EditNormalPostRequestDto(
            title = "postOneEdit",
            content = "postOneEdit",
            isAnon = false,
            commentOn = true,
            photoList = listOf(),
        )
        every { normalPostRepository.findByIdAndStatus(normalPostOne.id!!, PostStatus.NORMAL) } returns normalPostOne

        // when
        val exception = shouldThrow<UnAuthorizedException> {
            postService.editNormalPost(userTwo.email, normalPostOne.id!!, editNormalPostRequestDto)
        }

        // then
        verify(exactly = 1) { userRepository.findByEmail(userTwo.email) }
        verify(exactly = 1) { normalPostRepository.findByIdAndStatus(normalPostOne.id!!, PostStatus.NORMAL) }
        exception.log shouldBe "해당 글의 주인이 아닙니다."
    }

    @Test
    @DisplayName("글 추천 - 정상")
    fun likePostNormal() {
        // given
        val likePost = LikePost(
            user = userTwo,
            post = normalPostOne,
        )

        every {
            basePostRepository.findByIdAndStatusFetchLikeList(
                normalPostOne.id!!,
                PostStatus.NORMAL,
            )
        } returns normalPostOne
        every { likePostRepository.findByUserAndPost(userTwo, normalPostOne) } returns null
        every { likePostRepository.save(any()) } returns likePost

        // when

        postService.likePost(userTwo.email, normalPostOne.id!!)

        // then
        verify(exactly = 1) { userRepository.findByEmailFetchLikeList(userTwo.email) }
        verify(exactly = 1) { basePostRepository.findByIdAndStatusFetchLikeList(normalPostOne.id!!, PostStatus.NORMAL) }
        verify(exactly = 1) { likePostRepository.findByUserAndPost(userTwo, normalPostOne) }
        verify(exactly = 1) { likePostRepository.save(any()) }

        normalPostOne.likeList.size shouldBe 1
        normalPostOne.likeList[0].user shouldBe userTwo
        userTwo.likePostList.size shouldBe 1
        userTwo.likePostList[0].post shouldBe normalPostOne
    }

    @Test
    @DisplayName("글 추천 - 존재하지 않는 글 찾음")
    fun likePostErrorOne() {
        // given
        val likePost = LikePost(
            user = userTwo,
            post = normalPostOne,
        )
        val postId = 2L

        every { userRepository.findByEmailFetchLikeList(userTwo.email) } returns userTwo
        every { basePostRepository.findByIdAndStatusFetchLikeList(postId, PostStatus.NORMAL) } returns null

        // when
        val error = shouldThrow<EntityNotFoundException> {
            postService.likePost(userTwo.email, postId)
        }

        // then
        verify(exactly = 1) { userRepository.findByEmailFetchLikeList(userTwo.email) }
        verify(exactly = 1) { basePostRepository.findByIdAndStatusFetchLikeList(postId, PostStatus.NORMAL) }
        verify(exactly = 0) { likePostRepository.findByUserAndPost(userTwo, normalPostOne) }
        verify(exactly = 0) { likePostRepository.save(any()) }

        error.log shouldBe ErrorCode.NOT_FOUND_ENTITY.message

        normalPostOne.likeList.size shouldBe 0
        userTwo.likePostList.size shouldBe 0
    }

    @Test
    @DisplayName("글 추천 - 삭제된 글 찾음")
    fun likePostErrorTwo() {
        // given
        val likePost = LikePost(
            user = userTwo,
            post = normalPostOne,
        )
        val postId = 1L

        every { userRepository.findByEmailFetchLikeList(userTwo.email) } returns userTwo
        every { basePostRepository.findByIdAndStatusFetchLikeList(postId, PostStatus.NORMAL) } returns null

        // 4. 이미 추천한 글

        // when
        val error = shouldThrow<EntityNotFoundException> {
            postService.likePost(userTwo.email, normalPostOne.id!!)
        }

        // then
        verify(exactly = 1) { userRepository.findByEmailFetchLikeList(userTwo.email) }
        verify(exactly = 1) { basePostRepository.findByIdAndStatusFetchLikeList(normalPostOne.id!!, PostStatus.NORMAL) }
        verify(exactly = 0) { likePostRepository.findByUserAndPost(userTwo, normalPostOne) }
        verify(exactly = 0) { likePostRepository.save(any()) }

        error.log shouldBe ErrorCode.NOT_FOUND_ENTITY.message

        normalPostOne.likeList.size shouldBe 0
        userTwo.likePostList.size shouldBe 0
    }

    @Test
    @DisplayName("글 추천 - 존재하지 않는 유저")
    fun likePostErrorThree() {
        // given
        val likePost = LikePost(
            user = userTwo,
            post = normalPostOne,
        )
        val errorEmail = "error@test.com"

        every { userRepository.findByEmailFetchLikeList(errorEmail) } returns null

        // when
        val error = shouldThrow<EntityNotFoundException> {
            postService.likePost(errorEmail, normalPostOne.id!!)
        }

        // then
        verify(exactly = 1) { userRepository.findByEmailFetchLikeList(errorEmail) }
        verify(exactly = 0) { basePostRepository.findByIdAndStatusFetchLikeList(normalPostOne.id!!, PostStatus.NORMAL) }
        verify(exactly = 0) { likePostRepository.findByUserAndPost(userTwo, normalPostOne) }
        verify(exactly = 0) { likePostRepository.save(any()) }

        error.log shouldBe "$errorEmail 는 없는 유저 입니다."

        normalPostOne.likeList.size shouldBe 0
        userTwo.likePostList.size shouldBe 0
    }

    @Test
    @DisplayName("글 추천 - 이미 추천한 글")
    fun likePostErrorFour() {
        val likePost = LikePost(
            user = userTwo,
            post = normalPostOne,
        )

        every { userRepository.findByEmailFetchLikeList(userTwo.email) } returns userTwo
        every {
            basePostRepository.findByIdAndStatusFetchLikeList(
                normalPostOne.id!!,
                PostStatus.NORMAL,
            )
        } returns normalPostOne
        every { likePostRepository.findByUserAndPost(userTwo, normalPostOne) } returns null
        every { likePostRepository.save(any()) } returns likePost
        postService.likePost(userTwo.email, normalPostOne.id!!)
        clearMocks(likePostRepository, userRepository, basePostRepository)

        every { userRepository.findByEmailFetchLikeList(userTwo.email) } returns userTwo
        every {
            basePostRepository.findByIdAndStatusFetchLikeList(
                normalPostOne.id!!,
                PostStatus.NORMAL,
            )
        } returns normalPostOne
        every { likePostRepository.findByUserAndPost(userTwo, normalPostOne) } returns likePost
        every { likePostRepository.save(any()) } returns likePost

        // when
        val error = shouldThrow<ConditionConflictException> {
            postService.likePost(userTwo.email, normalPostOne.id!!)
        }

        // then
        verify(exactly = 1) { userRepository.findByEmailFetchLikeList(userTwo.email) }
        verify(exactly = 1) { basePostRepository.findByIdAndStatusFetchLikeList(normalPostOne.id!!, PostStatus.NORMAL) }
        verify(exactly = 1) { likePostRepository.findByUserAndPost(userTwo, normalPostOne) }
        verify(exactly = 0) { likePostRepository.save(any()) }

        error.log shouldBe "이미 추천을 했습니다."
        normalPostOne.likeList.size shouldBe 1
        userTwo.likePostList.size shouldBe 1
    }

    @Test
    fun 글_스크랩() {
    }

    @Test
    fun 글_스크랩_취소() {
    }

    @Test
    fun 글_단건_조회_회원() {
    }

    @Test
    fun 글_단건_조회_비회원() {
    }

    @Test
    fun 글_댓글_단독_조회() {
    }

    @Test
    fun 글_대량_조회() {
    }

    @Test
    fun 자신이_쓴_글_조회() {
    }

    @Test
    fun 자신이_스크랩_한_글_조회() { // given
    }
}
