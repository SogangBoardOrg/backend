package com.kotlin.boardproject.service

import com.kotlin.boardproject.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService
