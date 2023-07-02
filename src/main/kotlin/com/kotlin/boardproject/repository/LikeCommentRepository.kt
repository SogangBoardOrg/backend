package com.kotlin.boardproject.repository

import com.kotlin.boardproject.model.Comment
import com.kotlin.boardproject.model.LikeComment
import com.kotlin.boardproject.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface LikeCommentRepository : JpaRepository<LikeComment, Long> {
    fun findByUserAndComment(user: User, comment: Comment): LikeComment?
}
