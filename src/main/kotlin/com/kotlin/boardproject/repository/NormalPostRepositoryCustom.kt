package com.kotlin.boardproject.repository

import com.kotlin.boardproject.dto.FindNormalPostByQueryRequestDto
import com.kotlin.boardproject.dto.post.normalpost.FindNormalPostByQueryElementDto
import com.kotlin.boardproject.model.NormalPost
import com.kotlin.boardproject.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface NormalPostRepositoryCustom {
    fun findNormalPostByQuery(
        findNormalPostByQueryRequestDto: FindNormalPostByQueryRequestDto,
        pageable: Pageable,
    ): Page<NormalPost>

    fun findNormalPostByQueryV2(
        findNormalPostByQueryRequestDto: FindNormalPostByQueryRequestDto,
        userEmail: String?,
        pageable: Pageable,
    ): Page<FindNormalPostByQueryElementDto>
}
