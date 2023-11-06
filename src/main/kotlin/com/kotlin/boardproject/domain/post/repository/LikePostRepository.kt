package com.kotlin.boardproject.domain.post.repository

import com.kotlin.boardproject.domain.post.domain.BasePost
import com.kotlin.boardproject.domain.post.domain.LikePost
import com.kotlin.boardproject.domain.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface LikePostRepository : JpaRepository<LikePost, Long> {
    fun findByUserAndPost(user: User, post: BasePost): LikePost?
}
