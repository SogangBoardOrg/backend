package com.kotlin.boardproject.repository

import com.kotlin.boardproject.model.BasePost
import com.kotlin.boardproject.model.LikePost
import com.kotlin.boardproject.model.ScrapPost
import com.kotlin.boardproject.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ScrapPostRepository : JpaRepository<ScrapPost, Long> {
    fun findByUserAndPost(user: User, post: BasePost): ScrapPost?
    fun findByUser(user: User, pageable: Pageable): Page<ScrapPost>
    fun existsByUserAndPost(user: User, post: BasePost): Boolean
}
