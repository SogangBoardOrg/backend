package com.kotlin.boardproject.dto.post.normalpost

import com.kotlin.boardproject.model.NormalPost
import com.kotlin.boardproject.model.User
import org.springframework.data.domain.Page

data class NormalPostSearchResponseDto(
    val contents: List<OneNormalPostResponseDto>? = null,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val numberOfElements: Int,
    val size: Int,
) {
    companion object {
        fun createDtoFromPageable(pageData: Page<NormalPost>, writer: User?): NormalPostSearchResponseDto {
            val content = pageData.content.map {
                // it.toOneNormalPostResponseDto()
            }
            // TODO: 이거 내부로직 고치기

            return NormalPostSearchResponseDto(
                contents = null,
                currentPage = pageData.pageable.pageNumber,
                totalPages = pageData.totalPages,
                totalElements = pageData.totalElements,
                numberOfElements = pageData.numberOfElements,
                size = pageData.size,
            )
        }
    }
}
