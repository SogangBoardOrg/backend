package com.kotlin.boardproject.dto.post.normalpost

import java.time.LocalDateTime

data class OneNormalPostResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val writerName: String,
    val isAnon: Boolean,
    val isLiked: Boolean,
    val isScraped: Boolean,
    val isWriter: Boolean,
    val commentOn: Boolean,
    val createdTime: LocalDateTime,
    val lastModifiedTime: LocalDateTime?,
)
