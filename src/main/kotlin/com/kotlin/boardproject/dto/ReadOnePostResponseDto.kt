package com.kotlin.boardproject.dto

import java.time.LocalDateTime

data class ReadOnePostResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val writerName: String,
    val commentOn: Boolean,
    val createTime: LocalDateTime,
    val lastModifiedTime: LocalDateTime?,
)
