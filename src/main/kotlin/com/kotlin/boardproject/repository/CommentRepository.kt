package com.kotlin.boardproject.repository

import com.kotlin.boardproject.model.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long>
