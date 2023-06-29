package com.kotlin.boardproject.dto.post

import com.kotlin.boardproject.model.BasePost
import java.time.LocalDateTime

data class FindMyOneBasePostResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun fromBasePostToDto(post: BasePost): FindMyOneBasePostResponseDto {
            return FindMyOneBasePostResponseDto(
                id = post.id!!,
                title = post.title,
                content = post.title,
                createdAt = post.createdAt!!,
                updatedAt = post.updatedAt!!,
            )
        }
    }
}
