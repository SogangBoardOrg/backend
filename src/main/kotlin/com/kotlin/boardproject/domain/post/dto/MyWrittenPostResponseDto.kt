package com.kotlin.boardproject.domain.post.dto

import com.kotlin.boardproject.domain.post.domain.BasePost
import org.springframework.data.domain.Page

data class MyWrittenPostResponseDto(
    val contents: List<FindMyOneBasePostResponseDto>,
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
                    FindMyOneBasePostResponseDto.fromBasePostToDto(it)
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
