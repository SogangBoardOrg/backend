package com.kotlin.boardproject.domain.comment.repository

import com.kotlin.boardproject.domain.comment.domain.BlackComment
import com.kotlin.boardproject.domain.comment.domain.Comment
import com.kotlin.boardproject.domain.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface BlackCommentRepository : JpaRepository<BlackComment, Long> {
    fun findByUserAndComment(user: User, comment: Comment): BlackComment?
    fun existsByUserAndComment(user: User, comment: Comment): Boolean
    fun deleteByUserAndComment(user: User, comment: Comment)
}
