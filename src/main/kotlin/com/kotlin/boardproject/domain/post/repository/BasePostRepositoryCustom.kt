package com.kotlin.boardproject.domain.post.repository

import com.kotlin.boardproject.domain.post.dto.read.PostByQueryElementDto
import com.kotlin.boardproject.domain.post.dto.read.PostByQueryRequestDto
import com.kotlin.boardproject.global.enums.PostStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BasePostRepositoryCustom {
    fun findPostByQuery(
        postByQueryRequestDto: PostByQueryRequestDto,
        postStatus: PostStatus?,
        userEmail: String?,
        pageable: Pageable,
    ): Page<PostByQueryElementDto>
}
