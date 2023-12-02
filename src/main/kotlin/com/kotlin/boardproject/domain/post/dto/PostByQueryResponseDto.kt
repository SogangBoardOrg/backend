package com.kotlin.boardproject.domain.post.dto

import com.kotlin.boardproject.domain.post.dto.normalpost.NormalPostByQueryElementDto
import com.kotlin.boardproject.domain.post.dto.normalpost.NormalPostByQueryResponseDto
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

        fun createDtoFromPageable(pageData: Page<NormalPostByQueryElementDto>): NormalPostByQueryResponseDto {
            return NormalPostByQueryResponseDto(
                contents = pageData.content,
                currentPage = pageData.pageable.pageNumber,
                totalPages = pageData.totalPages,
                totalElements = pageData.totalElements,
                numberOfElements = pageData.numberOfElements,
                size = pageData.size,
            )
        }
    }
}
