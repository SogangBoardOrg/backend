package com.kotlin.boardproject.repository

import com.kotlin.boardproject.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
    fun findUserByProviderId(id: String): User?
    fun findByEmailOrProviderId(email: String, id: String): User?
    fun findUserByUsername(username: String): User?
}
