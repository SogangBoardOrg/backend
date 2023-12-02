package com.kotlin.boardproject.service

import com.kotlin.boardproject.domain.comment.repository.CommentRepository
import com.kotlin.boardproject.domain.post.domain.LikePost
import com.kotlin.boardproject.domain.post.domain.ScrapPost
import com.kotlin.boardproject.domain.post.dto.normalpost.CreatePostRequestDto
import com.kotlin.boardproject.domain.post.dto.normalpost.CreatePostResponseDto
import com.kotlin.boardproject.domain.post.dto.normalpost.EditPostRequestDto
import com.kotlin.boardproject.domain.post.dto.normalpost.EditPostResponseDto
import com.kotlin.boardproject.domain.post.repository.BasePostRepository
import com.kotlin.boardproject.domain.post.repository.BlackPostRepository
import com.kotlin.boardproject.domain.post.repository.LikePostRepository
import com.kotlin.boardproject.domain.post.repository.ScrapPostRepository
import com.kotlin.boardproject.domain.post.service.PostService
import com.kotlin.boardproject.domain.post.service.PostServiceImpl
import com.kotlin.boardproject.domain.schedule.repository.CourseRepository
import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.global.enums.ErrorCode
import com.kotlin.boardproject.global.enums.PostStatus
import com.kotlin.boardproject.global.enums.PostType
import com.kotlin.boardproject.global.exception.ConditionConflictException
import com.kotlin.boardproject.global.exception.EntityNotFoundException
import com.kotlin.boardproject.global.exception.UnAuthorizedException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

// beforespec, afterspec -> 모든 테스트 실행 전, 후 -> 제일 위
// beforeEach, AfterEach -> 테스트하나 마다

class PostServiceImplTest : BehaviorSpec(
    {
        isolationMode = IsolationMode.InstancePerTest

        val (userOne, userTwo, userThree) = makeUser()
        val (normalPostPresent, normalPostDeleted) = makeBasePost(userOne)

        val userRepository: UserRepository = mockk()
        val courseRepository: CourseRepository = mockk()
        val basePostRepository: BasePostRepository = mockk()
        val blackPostRepository: BlackPostRepository = mockk()
        val likePostRepository: LikePostRepository = mockk()
        val scrapPostRepository: ScrapPostRepository = mockk()
        val commentRepository: CommentRepository = mockk()

        lateinit var postService: PostService

        postService = PostServiceImpl(
            userRepository,
            courseRepository,
            basePostRepository,
            blackPostRepository,
            likePostRepository,
            scrapPostRepository,
            commentRepository,
        )

        setUserRepository(
            userOne,
            userTwo,
            userThree,
            userRepository,
        )

        setBasePostRepository(
            normalPostPresent,
            normalPostDeleted,
            basePostRepository,
        )

        given("일반 게시판 글 등록") {
            val createPostRequestDto = CreatePostRequestDto(
                title = "postOne",
                content = "postOne",
                isAnon = false,
                commentOn = true,
                photoList = listOf(),
                postType = PostType.NORMAL,
            )
            val postId = 1L
            every { basePostRepository.save(any()).id!! } returns postId
            `when`("정상 등록") {
                val data = postService.createPost(userOne.email, createPostRequestDto)
                then("통과") {
                    verify(exactly = 1) { basePostRepository.save(any()) }
                    data shouldBe CreatePostResponseDto(postId)
                }
            }
        }

        given("일반게시판 글 수정") {
            val editPostRequestDto = EditPostRequestDto(
                title = "postOneEdit",
                content = "postOneEdit",
                isAnon = false,
                commentOn = true,
                photoList = listOf(),
            )
            `when`("정상 수정") {
                val data = postService.editPost(userOne.email, normalPostPresent.id!!, editPostRequestDto)
                then("통과") {
                    verify(exactly = 1) { userRepository.findByEmail(userOne.email) }
                    verify(exactly = 1) {
                        basePostRepository.findByIdAndStatus(
                            normalPostPresent.id!!,
                            PostStatus.NORMAL,
                        )
                    }
                    data shouldBe EditPostResponseDto(normalPostPresent.id!!)
                }
            }

            `when`("없는 번호의 글 찾음") {
                // when
                val exception = shouldThrow<EntityNotFoundException> {
                    postService.editPost(userOne.email, nonExistPostId, editPostRequestDto)
                }
                // then
                then("글이 존재하지 않음") {
                    verify(exactly = 1) { userRepository.findByEmail(userOne.email) }
                    verify(exactly = 1) {
                        basePostRepository.findByIdAndStatus(
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
                    postService.editPost(userOne.email, normalPostDeleted.id!!, editPostRequestDto)
                }
                // then
                then("글이 존재하지 않음") {
                    verify(exactly = 1) { userRepository.findByEmail(userOne.email) }
                    verify(exactly = 1) {
                        basePostRepository.findByIdAndStatus(
                            normalPostDeleted.id!!,
                            PostStatus.NORMAL,
                        )
                    }
                    exception.log shouldBe "존재하지 않는 글 입니다."
                }
            }

            `when`("없는 유저 찾음") {
                val exception = shouldThrow<EntityNotFoundException> {
                    postService.editPost(nonExistUserEmail, normalPostPresent.id!!, editPostRequestDto)
                }
                then("유저가 존재하지 않음") {
                    verify(exactly = 1) { userRepository.findByEmail(nonExistUserEmail) }
                    verify(exactly = 0) { basePostRepository.findByIdAndStatus(nonExistPostId, PostStatus.NORMAL) }
                    exception.log shouldBe "$nonExistUserEmail 는 없는 유저 이메일 입니다."
                }
            }

            `when`("다른 유저의 글을 수정함") {
                val exception = shouldThrow<UnAuthorizedException> {
                    postService.editPost(userTwo.email, normalPostPresent.id!!, editPostRequestDto)
                }

                then("해당 글의 주인이 아니라고 에러") {
                    verify(exactly = 1) { userRepository.findByEmail(userTwo.email) }
                    verify(exactly = 1) {
                        basePostRepository.findByIdAndStatus(
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
    },
)
