package com.kotlin.boardproject.dto.post.normalpost

import com.kotlin.boardproject.dto.post.normalpost.FindNormalPostByQueryElementDto.Companion.fromNormalPostToQueryOneNormalPostResponseDto
import com.kotlin.boardproject.model.NormalPost
import com.kotlin.boardproject.model.User
import org.springframework.data.domain.Page

data class FindNormalPostByQueryResponseDto(
    val contents: List<FindNormalPostByQueryElementDto>? = null,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val numberOfElements: Int,
    val size: Int,
) {
    companion object {
        fun createDtoFromPageable(pageData: Page<NormalPost>, user: User?): FindNormalPostByQueryResponseDto {
            val content = pageData.content.map {
                fromNormalPostToQueryOneNormalPostResponseDto(it, user)
            }
            return FindNormalPostByQueryResponseDto(
                contents = content,
                currentPage = pageData.pageable.pageNumber,
                totalPages = pageData.totalPages,
                totalElements = pageData.totalElements,
                numberOfElements = pageData.numberOfElements,
                size = pageData.size,
            )
        }

        fun createDtoFromPageable(pageData: Page<FindNormalPostByQueryElementDto>): FindNormalPostByQueryResponseDto {
            return FindNormalPostByQueryResponseDto(
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
