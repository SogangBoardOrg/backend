package com.kotlin.boardproject.global.service

import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.global.util.UserPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username)
            ?: throw EntityNotFoundException("$username 을 가진 유저는 존재하지 않습니다.")

        return UserPrincipal.create(user)
    }
}
