package com.kotlin.boardproject.domain.comment.dto.read

import com.kotlin.boardproject.domain.comment.domain.Comment
import java.time.LocalDateTime

data class FindMyCommentResponseElementDto(
    val id: Long,
    val postId: Long,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun fromComment(comment: Comment): FindMyCommentResponseElementDto {
            return FindMyCommentResponseElementDto(
                id = comment.id!!,
                postId = comment.post.id!!,
                content = comment.content,
                createdAt = comment.createdAt!!,
                updatedAt = comment.updatedAt!!,
            )
        }
    }
}
