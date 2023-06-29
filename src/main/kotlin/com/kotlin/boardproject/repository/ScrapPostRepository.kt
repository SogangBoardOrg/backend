package com.kotlin.boardproject.repository

import com.kotlin.boardproject.model.BasePost
import com.kotlin.boardproject.model.ScrapPost
import com.kotlin.boardproject.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ScrapPostRepository : JpaRepository<ScrapPost, Long> {
    fun findByUserAndPost(user: User, post: BasePost): ScrapPost?

//    @Query(
//        """
//            SELECT DISTINCT s
//            FROM ScrapPost AS s
//            LEFT JOIN FETCH s.post
//            WHERE s.user = :user
//        """,
//    )
//    fun findByUserFetchPost(user: User, pageable: Pageable): Page<ScrapPost>
    fun existsByUserAndPost(user: User, post: BasePost): Boolean
}
