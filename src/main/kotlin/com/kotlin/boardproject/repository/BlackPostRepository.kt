package com.kotlin.boardproject.repository

import com.kotlin.boardproject.model.BasePost
import com.kotlin.boardproject.model.BlackPost
import com.kotlin.boardproject.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface BlackPostRepository : JpaRepository<BlackPost, Long> {
    fun findByUserAndPost(user: User, post: BasePost): BlackPost?
}
