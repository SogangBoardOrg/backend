package com.kotlin.boardproject.service

import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.domain.user.service.UserServiceImpl
import com.kotlin.boardproject.global.exception.ConditionConflictException
import com.kotlin.boardproject.global.exception.EntityNotFoundException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify

class UserServiceImplTest : BehaviorSpec(
    {
        isolationMode = IsolationMode.InstancePerTest

        val (userOne, userTwo, userThree) = makeUser()

        val userRepository: UserRepository = mockk()

        val userService = UserServiceImpl(userRepository)

        setUserRepository(
            userOne,
            userTwo,
            userThree,
            userRepository,
        )

        // 프로필 이미지 변경
        Given("프로필 이미지 변경") {
            When("프로필 이미지 변경") {
                userService.changeProfileImage(userOne.email, "newProfileImageUrl")

                Then("프로필 이미지 변경 완료") {
                    verify(exactly = 1) { userRepository.findByEmail(userOne.email) }
                }
            }

            When("없는 유저가 프로필 이미지 변경 시도") {
                val exception = shouldThrow<EntityNotFoundException> {
                    userService.changeProfileImage(nonExistUserEmail, "newProfileImageUrl")
                }

                Then("에러") {
                    verify(exactly = 1) { userRepository.findByEmail(nonExistUserEmail) }
                    exception.log shouldBe "해당하는 유저가 없습니다."
                }
            }
        }

        // 닉네임 변경
        Given("닉네임 변경") {
            When("성공") {
                userService.changeNickname(userOne.email, newtNickname)

                Then("프로필 이미지 변경 완료") {
                    verify(exactly = 1) { userRepository.findByEmail(userOne.email) }
                }
            }

            When("없는 유저가 닉네임 변경 시도") {
                val exception = shouldThrow<EntityNotFoundException> {
                    userService.changeNickname(nonExistUserEmail, "newNickname")
                }

                Then("에러") {
                    verify(exactly = 1) { userRepository.findByEmail(nonExistUserEmail) }
                    exception.log shouldBe "해당하는 유저가 없습니다."
                }
            }

            When("존재하는 닉네임 변경 시도") {
                val exception = shouldThrow<ConditionConflictException> {
                    userService.changeNickname(userOne.email, userTwo.nickname)
                }

                Then("에러") {
                    verify(exactly = 1) { userRepository.findByEmail(userOne.email) }
                    exception.log shouldBe "이미 존재하는 닉네임입니다."
                }
            }
        }
    },
)
