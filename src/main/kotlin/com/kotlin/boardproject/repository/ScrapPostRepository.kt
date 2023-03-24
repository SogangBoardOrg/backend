package com.kotlin.boardproject.repository

import com.kotlin.boardproject.model.BasePost
import com.kotlin.boardproject.model.ScrapPost
import com.kotlin.boardproject.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface ScrapPostRepository : JpaRepository<ScrapPost, Long> {
    fun findByUserAndPost(user: User, post: BasePost): ScrapPost?
    fun existsByUserAndPost(user: User, post: BasePost): Boolean
}
