package com.kotlin.boardproject.repository

import com.kotlin.boardproject.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long?> {
    fun findByEmail(email: String): User?
    fun findUserByProviderId(id: String): User?
    fun findByEmailOrProviderId(email: String, id: String): User?
    // override fun findByUserId
}
