package com.kotlin.boardproject.dto.comment

class CreateCommentRequestDto(
    val content: String,
    val isAnon: Boolean,
    val postId: Long,
    // TODO: 대댓글 기능을 위해서 선조 댓글과 직속 부모 댓글을 추가 예정
)
