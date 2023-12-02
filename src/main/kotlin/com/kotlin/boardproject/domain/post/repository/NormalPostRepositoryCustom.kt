package com.kotlin.boardproject.domain.post.repository

import com.kotlin.boardproject.domain.post.domain.NormalPost
import com.kotlin.boardproject.domain.post.dto.normalpost.NormalPostByQueryElementDto
import com.kotlin.boardproject.domain.post.dto.normalpost.PostByQueryRequestDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface NormalPostRepositoryCustom {
    // 문제가 생길시 Page<NormalPost>로 변경
    fun findNormalPostByQuery(
        postByQueryRequestDto: PostByQueryRequestDto,
        pageable: Pageable,
    ): Page<NormalPost>

    fun findNormalPostByQueryV2(
        postByQueryRequestDto: PostByQueryRequestDto,
        userEmail: String?,
        pageable: Pageable,
    ): Page<NormalPostByQueryElementDto>
}
