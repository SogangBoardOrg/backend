package com.kotlin.boardproject.repository

import com.kotlin.boardproject.common.enums.PostStatus
import com.kotlin.boardproject.model.BasePost
import com.kotlin.boardproject.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface BasePostRepository : JpaRepository<BasePost, Long> {
    fun findByIdAndStatus(id: Long, status: PostStatus): BasePost?
    fun findByWriterAndStatus(user: User, status: PostStatus, pageable: Pageable): Page<BasePost>
}
