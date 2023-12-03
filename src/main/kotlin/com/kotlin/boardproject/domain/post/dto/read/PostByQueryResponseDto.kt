package com.kotlin.boardproject.domain.post.dto.read

import org.springframework.data.domain.Page

data class PostByQueryResponseDto(
    val contents: List<PostByQueryElementDto>,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val numberOfElements: Int,
    val size: Int,
) {
    companion object {
        fun createDtoFromPageable(data: Page<PostByQueryElementDto>): PostByQueryResponseDto {
            return PostByQueryResponseDto(
                contents = data.content,
                currentPage = data.pageable.pageNumber,
                totalPages = data.totalPages,
                totalElements = data.totalElements,
                numberOfElements = data.numberOfElements,
                size = data.size,
            )
        }
    }
}
