package com.kotlin.boardproject.repository

import com.kotlin.boardproject.model.BlackComment
import com.kotlin.boardproject.model.Comment
import com.kotlin.boardproject.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface BlackCommentRepository : JpaRepository<BlackComment, Long> {
    fun findByUserAndComment(user: User, comment: Comment): BlackComment?
    fun existsByUserAndComment(user: User, comment: Comment): Boolean
    fun deleteByUserAndComment(user: User, comment: Comment)
}
