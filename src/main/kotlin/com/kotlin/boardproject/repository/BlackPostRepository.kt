package com.kotlin.boardproject.repository

import com.kotlin.boardproject.model.BlackPost
import org.springframework.data.jpa.repository.JpaRepository

interface BlackPostRepository : JpaRepository<BlackPost, Long>
