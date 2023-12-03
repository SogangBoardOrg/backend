package com.kotlin.boardproject.domain.comment.dto.read

import com.kotlin.boardproject.domain.comment.domain.Comment
import org.springframework.data.domain.Page

// 댓글 내용, 글 id, 생성, 삭제일시
data class MyCommentResponseDto(
    val contents: List<FindMyCommentResponseElementDto>? = mutableListOf(),
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val numberOfElements: Int,
    val size: Int,
) {
    companion object {
        fun createDtoFromPageable(
            commentList: Page<Comment>,
        ): MyCommentResponseDto {
            // 주인 글이 삭제된 댓글은 보여주지 않는다.
            val comments = commentList.content.map {
                FindMyCommentResponseElementDto.fromComment(it)
            }

            return MyCommentResponseDto(
                contents = comments,
                currentPage = commentList.pageable.pageNumber,
                totalPages = commentList.totalPages,
                totalElements = commentList.totalElements,
                numberOfElements = commentList.numberOfElements,
                size = commentList.size,
            )
        }
    }
}
