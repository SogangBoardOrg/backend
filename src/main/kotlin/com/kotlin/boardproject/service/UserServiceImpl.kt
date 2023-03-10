package com.kotlin.boardproject.service

import com.kotlin.boardproject.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {

    override fun join(username: String, pw: String) {
        // var user: User = User(username = username, password = pw)
        // userRepository.save(user)
        return
    }
}
