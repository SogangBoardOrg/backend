package com.kotlin.boardproject.dto.post.normalpost

import com.kotlin.boardproject.dto.post.normalpost.NormalPostByQueryElementDto.Companion.fromNormalPostToQueryOneNormalPostResponseDto
import com.kotlin.boardproject.model.NormalPost
import com.kotlin.boardproject.model.User
import org.springframework.data.domain.Page

data class NormalPostByQueryResponseDto(
    val contents: List<NormalPostByQueryElementDto>? = null,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val numberOfElements: Int,
    val size: Int,
) {
    companion object {
        fun createDtoFromPageable(pageData: Page<NormalPost>, user: User?): NormalPostByQueryResponseDto {
            val content = pageData.content.map {
                fromNormalPostToQueryOneNormalPostResponseDto(it, user)
            }
            return NormalPostByQueryResponseDto(
                contents = content,
                currentPage = pageData.pageable.pageNumber,
                totalPages = pageData.totalPages,
                totalElements = pageData.totalElements,
                numberOfElements = pageData.numberOfElements,
                size = pageData.size,
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
