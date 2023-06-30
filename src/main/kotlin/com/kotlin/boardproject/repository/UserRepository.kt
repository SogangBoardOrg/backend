package com.kotlin.boardproject.repository

import com.kotlin.boardproject.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
    fun findUserByProviderId(id: String): User?
    fun findByEmailOrProviderId(email: String, id: String): User?
    fun findUserByNickname(nickname: String): User?

    @Query(
        """ SELECT u
            FROM User AS u
            LEFT JOIN FETCH u.postList as pl
            WHERE u.email = :email
            """,
    )
    fun findByEmailFetchPostList(email: String): User?

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
}
