package com.kotlin.boardproject.repository

import com.kotlin.boardproject.model.BasePost
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<BasePost, Long>
