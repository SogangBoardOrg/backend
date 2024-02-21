package com.kotlin.boardproject.domain.post.dto.read

import com.kotlin.boardproject.global.enums.PostType

// title, content, writerName, normalType

data class PostByQueryRequestDto(
    val title: String?,
    val content: String?,
    val writerName: String?,
    val courseId: Long?,
    val postType: PostType?,
)
