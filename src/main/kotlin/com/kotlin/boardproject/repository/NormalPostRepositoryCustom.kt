package com.kotlin.boardproject.repository

import com.kotlin.boardproject.dto.post.normalpost.FindNormalPostByQueryElementDto
import com.kotlin.boardproject.dto.post.normalpost.FindNormalPostByQueryRequestDto
import com.kotlin.boardproject.model.NormalPost
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface NormalPostRepositoryCustom {
    // 문제가 생길시 Page<NormalPost>로 변경
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
