package com.kotlin.boardproject.domain.post.repository

import com.kotlin.boardproject.domain.post.dto.PostByQueryElementDto
import com.kotlin.boardproject.domain.post.dto.normalpost.PostByQueryRequestDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BasePostRepositoryCustom {
    fun findPostByQuery(
        postByQueryRequestDto: PostByQueryRequestDto,
        userEmail: String?,
        pageable: Pageable,
    ): Page<PostByQueryElementDto>
}
