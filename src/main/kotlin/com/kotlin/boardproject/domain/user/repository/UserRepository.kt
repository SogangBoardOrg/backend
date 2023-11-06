package com.kotlin.boardproject.domain.user.repository

import com.kotlin.boardproject.domain.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
    fun findByNickname(nickname: String): User?
    fun findUserByProviderId(id: String): User?
    fun findByEmailOrProviderId(email: String, id: String): User?

    @Query(
        """ SELECT u
            FROM User AS u
            LEFT JOIN FETCH u.likePostList as l
            LEFT JOIN FETCH l.post
            WHERE u.email = :email
            """,
    )
    fun findByEmailFetchLikeList(email: String): User?

    @Query(
        """ SELECT DISTINCT u
            FROM User AS u
            LEFT JOIN FETCH u.scrapList s
            LEFT JOIN FETCH s.post
            WHERE u.email = :email
            """,
    )
    fun findByEmailFetchScrapList(email: String): User?

    fun findByEmailOrNickname(email: String, nickname: String): User?
}
