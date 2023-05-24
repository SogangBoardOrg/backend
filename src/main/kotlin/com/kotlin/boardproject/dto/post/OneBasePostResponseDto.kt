package com.kotlin.boardproject.dto.post

import com.kotlin.boardproject.model.BasePost
import java.time.LocalDateTime

data class OneBasePostResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val createdTime: LocalDateTime,
    val lastModifiedTime: LocalDateTime?,
) {
    companion object {
        fun fromBasePostToDto(post: BasePost): OneBasePostResponseDto {
            return OneBasePostResponseDto(
                id = post.id!!,
                title = post.title,
                content = post.title,
                createdTime = post.createdAt!!,
                lastModifiedTime = post.updatedAt,
            )
        }
    }
}
