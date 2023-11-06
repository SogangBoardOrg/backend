package com.kotlin.boardproject.domain.post.repository

import com.kotlin.boardproject.domain.post.domain.BasePost
import com.kotlin.boardproject.domain.post.domain.BlackPost
import com.kotlin.boardproject.domain.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface BlackPostRepository : JpaRepository<BlackPost, Long> {
    fun findByUserAndPost(user: User, post: BasePost): BlackPost?
}
