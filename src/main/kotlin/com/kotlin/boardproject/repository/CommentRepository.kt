package com.kotlin.boardproject.repository

import com.kotlin.boardproject.common.enums.PostStautus
import com.kotlin.boardproject.model.Comment
import com.kotlin.boardproject.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByIdAndStatus(id: Long, status: PostStautus): Comment?
}
