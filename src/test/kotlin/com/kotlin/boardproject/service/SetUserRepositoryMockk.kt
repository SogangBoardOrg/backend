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

    every { userRepository.findByEmailFetchScrapList(userOne.email) } returns userOne
    every { userRepository.findByEmailFetchScrapList(userTwo.email) } returns userTwo
    every { userRepository.findByEmailFetchScrapList(userThree.email) } returns userThree
    every { userRepository.findByEmailFetchScrapList(nonExistUserEmail) } returns null

    every { userRepository.existsByNickname(userOne.nickname) } returns true
    every { userRepository.existsByNickname(userTwo.nickname) } returns true
    every { userRepository.existsByNickname(userThree.nickname) } returns true
    every { userRepository.existsByNickname(newtNickname) } returns false
}
