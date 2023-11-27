package com.kotlin.boardproject.service

import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.domain.user.repository.UserRepository
import io.mockk.every

fun setUserRepository(
    userOne: User,
    userTwo: User,
    userThree: User,
    userRepository: UserRepository,
) {
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
