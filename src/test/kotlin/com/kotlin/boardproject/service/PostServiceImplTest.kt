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
import com.kotlin.boardproject.model.ScrapPost
import com.kotlin.boardproject.model.User
import com.kotlin.boardproject.repository.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

// beforespec, afterspec -> 모든 테스트 실행 전, 후 -> 제일 위
// beforeEach, AfterEach -> 테스트하나 마다

class PostServiceImplTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerTest

    val (userOne, userTwo, userThree) = makeUser()
    val (normalPostPresent, normalPostDeleted) = makePost(userOne)
    val nonExistPostId = -1L
    val nonExistUserEmail = "fail@test.com"

    normalPostPresent.id = 1L
    normalPostDeleted.id = 2L
    normalPostDeleted.status = PostStatus.DELETED

    val userRepository: UserRepository = mockk()
    val normalPostRepository: NormalPostRepository = mockk()
    val basePostRepository: BasePostRepository = mockk()
    val blackPostRepository: BlackPostRepository = mockk()
    val likePostRepository: LikePostRepository = mockk()
    val scrapPostRepository: ScrapPostRepository = mockk()
    val commentRepository: CommentRepository = mockk()

    lateinit var postService: PostService

    fun setUserRepository() {
        every { userRepository.findByEmail(userOne.email) } returns userOne
        every { userRepository.findByEmail(userTwo.email) } returns userTwo
        every { userRepository.findByEmail(userThree.email) } returns userThree
        every { userRepository.findByEmail(nonExistUserEmail) } returns null

        every { userRepository.findByEmailFetchLikeList(userOne.email) } returns userOne
        every { userRepository.findByEmailFetchLikeList(userTwo.email) } returns userTwo
        every { userRepository.findByEmailFetchLikeList(userThree.email) } returns userThree
        every { userRepository.findByEmailFetchLikeList(nonExistUserEmail) } returns null

        every { userRepository.findByEmailFetchScrapList(userOne.email) } returns userOne
        every { userRepository.findByEmailFetchScrapList(userTwo.email) } returns userTwo
        every { userRepository.findByEmailFetchScrapList(userThree.email) } returns userThree
        every { userRepository.findByEmailFetchScrapList(nonExistUserEmail) } returns null
    }

    fun setNormalPostRepository() {
        every {
            normalPostRepository.findByIdAndStatus(
                normalPostPresent.id!!,
                PostStatus.NORMAL,
            )
        } returns normalPostPresent
        every {
            normalPostRepository.findByIdAndStatus(
                normalPostDeleted.id!!,
                PostStatus.NORMAL,
            )
        } returns null
        every {
            normalPostRepository.findByIdAndStatus(
                nonExistPostId,
                PostStatus.NORMAL,
            )
        } returns null
    }

    fun setBasePostRepository() {
        every {
            basePostRepository.findByIdAndStatus(
                normalPostPresent.id!!,
                PostStatus.NORMAL,
            )
        } returns normalPostPresent
        every {
            basePostRepository.findByIdAndStatusFetchLikeList(
                normalPostPresent.id!!,
                PostStatus.NORMAL,
            )
        } returns normalPostPresent
        every {
            basePostRepository.findByIdAndStatusFetchScrapList(
                normalPostPresent.id!!,
                PostStatus.NORMAL,
            )
        } returns normalPostPresent

        every {
            basePostRepository.findByIdAndStatus(
                normalPostDeleted.id!!,
                PostStatus.NORMAL,
            )
        } returns null
        every {
            basePostRepository.findByIdAndStatusFetchLikeList(
                normalPostDeleted.id!!,
                PostStatus.NORMAL,
            )
        } returns null
        every {
            basePostRepository.findByIdAndStatusFetchScrapList(
                normalPostDeleted.id!!,
                PostStatus.NORMAL,
            )
        } returns null

        every {
            basePostRepository.findByIdAndStatus(
                nonExistPostId,
                PostStatus.NORMAL,
            )
        } returns null
        every {
            basePostRepository.findByIdAndStatusFetchLikeList(
                nonExistPostId,
                PostStatus.NORMAL,
            )
        } returns null
        every {
            basePostRepository.findByIdAndStatusFetchScrapList(
                nonExistPostId,
                PostStatus.NORMAL,
            )
        } returns null
    }

    postService = PostServiceImpl(
        normalPostRepository,
        userRepository,
        basePostRepository,
        blackPostRepository,
        likePostRepository,
        scrapPostRepository,
        commentRepository,
    )

    setUserRepository()
    setNormalPostRepository()
    setBasePostRepository()

    given("일반 게시판 글 등록") {
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
        `when`("정상 등록") {
            val data = postService.createNormalPost(userOne.email, createNormalPostRequestDto)
            then("통과") {
                verify(exactly = 1) { normalPostRepository.save(any()) }
                data shouldBe CreateNormalPostResponseDto(postId)
            }
        }
    }

    given("일반게시판 글 수정") {
        val editNormalPostRequestDto = EditNormalPostRequestDto(
            title = "postOneEdit",
            content = "postOneEdit",
            isAnon = false,
            commentOn = true,
            photoList = listOf(),
        )
        `when`("정상 수정") {
            val data = postService.editNormalPost(userOne.email, normalPostPresent.id!!, editNormalPostRequestDto)
            then("통과") {
                verify(exactly = 1) { userRepository.findByEmail(userOne.email) }
                verify(exactly = 1) {
                    normalPostRepository.findByIdAndStatus(
                        normalPostPresent.id!!,
                        PostStatus.NORMAL,
                    )
                }
                data shouldBe EditNormalPostResponseDto(normalPostPresent.id!!)
            }
        }

        `when`("없는 번호의 글 찾음") {
            // when
            val exception = shouldThrow<EntityNotFoundException> {
                postService.editNormalPost(userOne.email, nonExistPostId, editNormalPostRequestDto)
            }
            // then
            then("글이 존재하지 않음") {
                verify(exactly = 1) { userRepository.findByEmail(userOne.email) }
                verify(exactly = 1) {
                    normalPostRepository.findByIdAndStatus(
                        nonExistPostId,
                        PostStatus.NORMAL,
                    )
                }
                exception.log shouldBe "존재하지 않는 글 입니다."
            }
        }

        `when`("삭제된 글 찾음") {
            // when
            val exception = shouldThrow<EntityNotFoundException> {
                postService.editNormalPost(userOne.email, normalPostDeleted.id!!, editNormalPostRequestDto)
            }
            // then
            then("글이 존재하지 않음") {
                verify(exactly = 1) { userRepository.findByEmail(userOne.email) }
                verify(exactly = 1) {
                    normalPostRepository.findByIdAndStatus(
                        normalPostDeleted.id!!,
                        PostStatus.NORMAL,
                    )
                }
                exception.log shouldBe "존재하지 않는 글 입니다."
            }
        }

        `when`("없는 유저 찾음") {
            val exception = shouldThrow<EntityNotFoundException> {
                postService.editNormalPost(nonExistUserEmail, normalPostPresent.id!!, editNormalPostRequestDto)
            }
            then("유저가 존재하지 않음") {
                verify(exactly = 1) { userRepository.findByEmail(nonExistUserEmail) }
                verify(exactly = 0) { normalPostRepository.findByIdAndStatus(nonExistPostId, PostStatus.NORMAL) }
                exception.log shouldBe "$nonExistUserEmail 는 없는 유저 이메일 입니다."
            }
        }

        `when`("다른 유저의 글을 수정함") {
            val exception = shouldThrow<UnAuthorizedException> {
                postService.editNormalPost(userTwo.email, normalPostPresent.id!!, editNormalPostRequestDto)
            }

            then("해당 글의 주인이 아니라고 에러") {
                verify(exactly = 1) { userRepository.findByEmail(userTwo.email) }
                verify(exactly = 1) {
                    normalPostRepository.findByIdAndStatus(
                        normalPostPresent.id!!,
                        PostStatus.NORMAL,
                    )
                }
                exception.log shouldBe "해당 글의 주인이 아닙니다."
            }
        }
    }

    given("글 추천") {
        val likePost = LikePost(
            user = userTwo,
            post = normalPostPresent,
        )

        every { likePostRepository.findByUserAndPost(userTwo, normalPostPresent) } returns null
        every { likePostRepository.save(any()) } returns likePost

        `when`("정상") {
            postService.likePost(userTwo.email, normalPostPresent.id!!)
            then("통과") {
                verify(exactly = 1) { userRepository.findByEmailFetchLikeList(userTwo.email) }
                verify(exactly = 1) {
                    basePostRepository.findByIdAndStatusFetchLikeList(
                        normalPostPresent.id!!,
                        PostStatus.NORMAL,
                    )
                }
                verify(exactly = 1) { likePostRepository.findByUserAndPost(userTwo, normalPostPresent) }
                verify(exactly = 1) { likePostRepository.save(any()) }

                normalPostPresent.likeList.size shouldBe 1
                normalPostPresent.likeList[0].user shouldBe userTwo
                userTwo.likePostList.size shouldBe 1
                userTwo.likePostList[0].post shouldBe normalPostPresent
            }
        }

        `when`("없는 번호의 글 추천") {
            val error = shouldThrow<EntityNotFoundException> {
                postService.likePost(userTwo.email, nonExistPostId)
            }
            then("글이 존재하지 않음") {
                verify(exactly = 1) { userRepository.findByEmailFetchLikeList(userTwo.email) }
                verify(exactly = 1) {
                    basePostRepository.findByIdAndStatusFetchLikeList(
                        nonExistPostId,
                        PostStatus.NORMAL,
                    )
                }
                verify(exactly = 0) { likePostRepository.findByUserAndPost(userTwo, any()) }
                verify(exactly = 0) { likePostRepository.save(any()) }

                error.log shouldBe ErrorCode.NOT_FOUND_ENTITY.message

                userTwo.likePostList.size shouldBe 0
            }
        }

        `when`("삭제된 글 추천") {
            val error = shouldThrow<EntityNotFoundException> {
                postService.likePost(userTwo.email, normalPostDeleted.id!!)
            }
            then("글이 존재하지 않음") {
                verify(exactly = 1) { userRepository.findByEmailFetchLikeList(userTwo.email) }
                verify(exactly = 1) {
                    basePostRepository.findByIdAndStatusFetchLikeList(
                        normalPostDeleted.id!!,
                        PostStatus.NORMAL,
                    )
                }
                verify(exactly = 0) { likePostRepository.findByUserAndPost(userTwo, any()) }
                verify(exactly = 0) { likePostRepository.save(any()) }

                error.log shouldBe ErrorCode.NOT_FOUND_ENTITY.message

                userTwo.likePostList.size shouldBe 0
            }
        }

        `when`("없는 유저 찾음") {
            val error = shouldThrow<EntityNotFoundException> {
                postService.likePost(nonExistUserEmail, normalPostPresent.id!!)
            }
            then("유저가 존재하지 않음") {

                verify(exactly = 1) { userRepository.findByEmailFetchLikeList(nonExistUserEmail) }
                verify(exactly = 0) {
                    basePostRepository.findByIdAndStatusFetchLikeList(
                        normalPostPresent.id!!,
                        PostStatus.NORMAL,
                    )
                }
                verify(exactly = 0) { likePostRepository.findByUserAndPost(userTwo, normalPostPresent) }
                verify(exactly = 0) { likePostRepository.save(any()) }

                error.log shouldBe "$nonExistUserEmail 는 없는 유저 입니다."

                normalPostPresent.likeList.size shouldBe 0
                userTwo.likePostList.size shouldBe 0
            }
        }

        `when`("이미 추천했음") {
            every { likePostRepository.findByUserAndPost(userTwo, normalPostPresent) } returns likePost
            val error = shouldThrow<ConditionConflictException> {
                postService.likePost(userTwo.email, normalPostPresent.id!!)
            }

            then("이미 추천을 한 글") {
                verify(exactly = 1) { userRepository.findByEmailFetchLikeList(userTwo.email) }
                verify(exactly = 1) {
                    basePostRepository.findByIdAndStatusFetchLikeList(
                        normalPostPresent.id!!,
                        PostStatus.NORMAL,
                    )
                }
                verify(exactly = 1) { likePostRepository.findByUserAndPost(userTwo, normalPostPresent) }
                verify(exactly = 0) { likePostRepository.save(any()) }

                error.log shouldBe "이미 추천을 했습니다."
            }
        }
    }

    given("글 추천 취소") {
    }

    given("글 스크랩") {
        val scrapPost = ScrapPost(
            user = userTwo,
            post = normalPostPresent,
        )

        every { scrapPostRepository.findByUserAndPost(userTwo, normalPostPresent) } returns null
        every { scrapPostRepository.save(any()) } returns scrapPost

        `when`("정상") {
            postService.scrapPost(userTwo.email, normalPostPresent.id!!)
            then("통과") {
                verify(exactly = 1) { userRepository.findByEmailFetchScrapList(userTwo.email) }
                verify(exactly = 1) {
                    basePostRepository.findByIdAndStatusFetchScrapList(
                        normalPostPresent.id!!,
                        PostStatus.NORMAL,
                    )
                }
                verify(exactly = 1) { scrapPostRepository.findByUserAndPost(userTwo, normalPostPresent) }
                verify(exactly = 1) { scrapPostRepository.save(any()) }

                normalPostPresent.scrapList.size shouldBe 1
                normalPostPresent.scrapList[0].user shouldBe userTwo
                userTwo.scrapList.size shouldBe 1
                userTwo.scrapList[0].post shouldBe normalPostPresent
            }
        }

        `when`("없는 번호의 글 스크랩") {
            val error = shouldThrow<EntityNotFoundException> {
                postService.scrapPost(userTwo.email, nonExistPostId)
            }
            then("글이 존재하지 않음") {
                verify(exactly = 1) { userRepository.findByEmailFetchScrapList(userTwo.email) }
                verify(exactly = 1) {
                    basePostRepository.findByIdAndStatusFetchScrapList(
                        nonExistPostId,
                        PostStatus.NORMAL,
                    )
                }
                verify(exactly = 0) { scrapPostRepository.findByUserAndPost(userTwo, any()) }
                verify(exactly = 0) { scrapPostRepository.save(any()) }

                error.log shouldBe ErrorCode.NOT_FOUND_ENTITY.message

                userTwo.scrapList.size shouldBe 0
            }
        }

        `when`("삭제된 글 스크랩") {
            val error = shouldThrow<EntityNotFoundException> {
                postService.scrapPost(userTwo.email, normalPostDeleted.id!!)
            }
            then("글이 존재하지 않음") {
                verify(exactly = 1) { userRepository.findByEmailFetchScrapList(userTwo.email) }
                verify(exactly = 1) {
                    basePostRepository.findByIdAndStatusFetchScrapList(
                        normalPostDeleted.id!!,
                        PostStatus.NORMAL,
                    )
                }
                verify(exactly = 0) { scrapPostRepository.findByUserAndPost(userTwo, any()) }
                verify(exactly = 0) { scrapPostRepository.save(any()) }

                error.log shouldBe ErrorCode.NOT_FOUND_ENTITY.message

                userTwo.scrapList.size shouldBe 0
            }
        }

        `when`("없는 유저 찾음") {
            val error = shouldThrow<EntityNotFoundException> {
                postService.scrapPost(nonExistUserEmail, normalPostPresent.id!!)
            }
            then("유저가 존재하지 않음") {

                verify(exactly = 1) { userRepository.findByEmailFetchScrapList(nonExistUserEmail) }
                verify(exactly = 0) {
                    basePostRepository.findByIdAndStatusFetchScrapList(
                        normalPostPresent.id!!,
                        PostStatus.NORMAL,
                    )
                }
                verify(exactly = 0) { scrapPostRepository.findByUserAndPost(userTwo, normalPostPresent) }
                verify(exactly = 0) { scrapPostRepository.save(any()) }

                error.log shouldBe "$nonExistUserEmail 존재하지 않는 유저 입니다."

                normalPostPresent.scrapList.size shouldBe 0
                userTwo.scrapList.size shouldBe 0
            }
        }

        `when`("이미 스크랩 했음") {
            every { scrapPostRepository.findByUserAndPost(userTwo, normalPostPresent) } returns scrapPost
            val error = shouldThrow<ConditionConflictException> {
                postService.scrapPost(userTwo.email, normalPostPresent.id!!)
            }
            then("이미 스크랩을 하였음") {
                verify(exactly = 1) { userRepository.findByEmailFetchScrapList(userTwo.email) }
                verify(exactly = 1) {
                    basePostRepository.findByIdAndStatusFetchScrapList(
                        normalPostPresent.id!!,
                        PostStatus.NORMAL,
                    )
                }
                verify(exactly = 1) { scrapPostRepository.findByUserAndPost(userTwo, normalPostPresent) }
                verify(exactly = 0) { scrapPostRepository.save(any()) }

                error.log shouldBe "이미 스크랩을 하였습니다."
            }
        }
    }

    given("글 스크랩 취소") {}

    /*
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
    fun 자신이_스크랩_한_글_조회()
    }*/
})

fun makeUser(): List<User> {
    val userOne = User(
        id = UUID.randomUUID(),
        email = "userOne@test.com",
        nickname = "userOne",
        role = Role.ROLE_VERIFIED_USER,
        providerType = ProviderType.LOCAL,
    )

    val userTwo = User(
        id = UUID.randomUUID(),
        email = "userTwo@test.com",
        nickname = "userTwo",
        role = Role.ROLE_VERIFIED_USER,
        providerType = ProviderType.LOCAL,
    )

    val userThree = User(
        id = UUID.randomUUID(),
        email = "userThree@test.com",
        nickname = "userThree",
        role = Role.ROLE_VERIFIED_USER,
        providerType = ProviderType.LOCAL,
    )

    return listOf(userOne, userTwo, userThree)
}

fun makePost(writer: User): List<NormalPost> {
    val one = NormalPost(
        title = "postOne",
        content = "postOne",
        writer = writer,
        isAnon = false,
        commentOn = true,
        normalType = NormalType.FREE,
    )
    val two = NormalPost(
        title = "postOne",
        content = "postOne",
        writer = writer,
        isAnon = false,
        commentOn = true,
        normalType = NormalType.FREE,
    )
    return listOf(one, two)
}
