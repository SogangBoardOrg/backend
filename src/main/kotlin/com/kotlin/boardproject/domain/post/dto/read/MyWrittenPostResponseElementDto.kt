package com.kotlin.boardproject.domain.post.dto.read

import com.kotlin.boardproject.domain.post.domain.BasePost
import java.time.LocalDateTime

data class MyWrittenPostResponseElementDto(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun fromBasePostToDto(post: BasePost): MyWrittenPostResponseElementDto {
            return MyWrittenPostResponseElementDto(
                id = post.id!!,
                title = post.title,
                content = post.content,
                createdAt = post.createdAt!!,
                updatedAt = post.updatedAt!!,
            )
        }
    }
}
