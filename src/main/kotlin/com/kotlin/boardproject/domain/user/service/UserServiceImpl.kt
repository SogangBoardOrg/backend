package com.kotlin.boardproject.domain.user.service

import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.global.enums.ErrorCode
import com.kotlin.boardproject.global.exception.ConditionConflictException
import com.kotlin.boardproject.global.exception.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {

    @Transactional
    override fun changeProfileImage(userEmail: String, profileImageUrl: String) {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("해당하는 유저가 없습니다.")
        user.profileImageUrl = profileImageUrl
    }

    @Transactional
    override fun changeNickname(userEmail: String, nickname: String) {
        val user = userRepository.findByEmail(userEmail)
            ?: throw EntityNotFoundException("해당하는 유저가 없습니다.")

        val exists = userRepository.existsByNickname(nickname)

        if (exists) {
            throw ConditionConflictException(ErrorCode.USERNAME_DUPLICATED, "이미 존재하는 닉네임입니다.")
        }

        user.nickname = nickname
    }
}
