package com.kotlin.boardproject.dto

import com.kotlin.boardproject.dto.post.OneBasePostResponseDto
import com.kotlin.boardproject.model.BasePost
import org.springframework.data.domain.Page

data class MyWrittenPostResponseDto(
    val contents: List<OneBasePostResponseDto>? = null,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val numberOfElements: Int,
    val size: Int,
) {
    companion object {
        fun createDtoFromPageable(postList: Page<BasePost>): MyWrittenPostResponseDto {
            return MyWrittenPostResponseDto(
                contents = postList.content.map {
                    it.toOneBasePostResponseDto()
                },
                currentPage = postList.pageable.pageNumber,
                totalPages = postList.totalPages,
                totalElements = postList.totalElements,
                numberOfElements = postList.numberOfElements,
                size = postList.size,
            )
        }
    }
}
