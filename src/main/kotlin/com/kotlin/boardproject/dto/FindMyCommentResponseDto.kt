package com.kotlin.boardproject.dto

import com.kotlin.boardproject.common.enums.PostStatus
import com.kotlin.boardproject.model.Comment
import org.springframework.data.domain.Page

// 댓글 내용, 글 id, 생성, 삭제일시
data class FindMyCommentResponseDto(
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
        ): FindMyCommentResponseDto {
            // 주인 글이 삭제된 댓글은 보여주지 않는다.

            val comments = commentList.content.filter {
                it.post.status == PostStatus.NORMAL
            }.map {
                FindMyCommentResponseElementDto.fromComment(it)
            }

            return FindMyCommentResponseDto(
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
