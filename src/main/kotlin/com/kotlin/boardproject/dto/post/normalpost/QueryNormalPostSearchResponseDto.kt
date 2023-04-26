package com.kotlin.boardproject.dto.post.normalpost

import com.kotlin.boardproject.model.NormalPost
import com.kotlin.boardproject.model.User
import org.springframework.data.domain.Page

data class QueryNormalPostSearchResponseDto(
    val contents: List<QueryOneNormalPostResponseDto>? = null,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val numberOfElements: Int,
    val size: Int,
) {
    companion object {
        fun createDtoFromPageable(pageData: Page<NormalPost>, user: User?): QueryNormalPostSearchResponseDto {
            val content = pageData.content.map {
                it.toQueryOneNormalPostResponseDto(user)
            }
            // TODO: 이거 내부로직 고치기

            return QueryNormalPostSearchResponseDto(
                contents = content,
                currentPage = pageData.pageable.pageNumber,
                totalPages = pageData.totalPages,
                totalElements = pageData.totalElements,
                numberOfElements = pageData.numberOfElements,
                size = pageData.size,
            )
        }
    }
}
