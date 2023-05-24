package com.kotlin.boardproject.dto.comment

import java.time.LocalDateTime

data class CommentDto(
    // TODO: 좋아요 여부와 좋아요 개수는 어떻게 처리할까?
    val id: Long,
    val content: String,
    val isAnon: Boolean,
    val writerName: String,
    val isLiked: Boolean?,
    val isWriter: Boolean?, // 로그인 한 사람이 댓글 작성자
    val createdTime: LocalDateTime,
    val lastModifiedTime: LocalDateTime,
    val parentId: Long?,
    val ancestorId: Long?,
    val likeCnt: Int?,
    val child: MutableList<CommentDto> = mutableListOf(),
)
