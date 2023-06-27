package com.kotlin.boardproject.dto

import com.kotlin.boardproject.common.enums.NormalType

// title, content, writerName, normalType

class FindNormalPostByQueryRequestDto(
    val title: String?,
    val content: String?,
    val writerName: String?,
    val normalType: NormalType,
)
