package com.kotlin.boardproject.domain.comment.repository

import com.kotlin.boardproject.domain.comment.domain.Comment
import com.kotlin.boardproject.domain.comment.domain.LikeComment
import com.kotlin.boardproject.domain.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface LikeCommentRepository : JpaRepository<LikeComment, Long> {
    fun findByUserAndComment(user: User, comment: Comment): LikeComment?
}
