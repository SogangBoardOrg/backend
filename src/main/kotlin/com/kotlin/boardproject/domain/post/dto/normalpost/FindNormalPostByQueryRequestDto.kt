package com.kotlin.boardproject.domain.post.dto.normalpost

import com.kotlin.boardproject.global.enums.NormalType

// title, content, writerName, normalType

class FindNormalPostByQueryRequestDto(
    val title: String?,
    val content: String?,
    val writerName: String?,
    val normalType: NormalType,
)
