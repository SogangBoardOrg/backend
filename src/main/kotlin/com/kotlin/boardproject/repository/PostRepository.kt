package com.kotlin.boardproject.repository

import com.kotlin.boardproject.common.enums.PostStautus
import com.kotlin.boardproject.model.BasePost
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PostRepository : JpaRepository<BasePost, Long> {
    fun findByIdAndStatus(id: Long, status: PostStautus): BasePost?
}
