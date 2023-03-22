package com.kotlin.boardproject.dto.post.normalpost

import java.time.LocalDateTime

data class OneNormalPostResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val writerName: String,
    val isAnon: Boolean,
    val commentOn: Boolean,
    val createTime: LocalDateTime,
    val lastModifiedTime: LocalDateTime?,
)
