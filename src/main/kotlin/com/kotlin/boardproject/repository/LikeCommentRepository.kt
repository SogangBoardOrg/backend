package com.kotlin.boardproject.repository

import com.kotlin.boardproject.model.*
import org.springframework.data.jpa.repository.JpaRepository

interface LikeCommentRepository : JpaRepository<LikeComment, Long> {
    fun findByUserAndComment(user: User, comment: Comment): LikeComment?
    fun existsByUserAndComment(user: User, comment: Comment): Boolean
    fun deleteByUserAndComment(user: User, comment: Comment)
}
