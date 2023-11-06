package com.kotlin.boardproject.domain.user.service

import com.kotlin.boardproject.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService
