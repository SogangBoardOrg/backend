package com.kotlin.boardproject.repository

import com.kotlin.boardproject.model.BasePost
import com.kotlin.boardproject.model.LikePost
import com.kotlin.boardproject.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface LikePostRepository : JpaRepository<LikePost, Long> {
    fun findByUserAndPost(user: User, post: BasePost): LikePost?
}
