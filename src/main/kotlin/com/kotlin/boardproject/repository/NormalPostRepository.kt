package com.kotlin.boardproject.repository

import com.kotlin.boardproject.common.enums.PostStautus
import com.kotlin.boardproject.model.NormalPost
import org.springframework.data.jpa.repository.JpaRepository

interface NormalPostRepository : JpaRepository<NormalPost, Long> {
    fun findByIdAndStatus(id: Long, status: PostStautus): NormalPost?
}
