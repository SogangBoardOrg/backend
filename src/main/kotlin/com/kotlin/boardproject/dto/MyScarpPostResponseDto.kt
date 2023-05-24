package com.kotlin.boardproject.dto

import com.kotlin.boardproject.common.enums.PostStatus
import com.kotlin.boardproject.dto.post.OneBasePostResponseDto
import com.kotlin.boardproject.model.ScrapPost
import org.springframework.data.domain.Page

data class MyScarpPostResponseDto(
    val contents: List<OneBasePostResponseDto>? = null,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val numberOfElements: Int,
    val size: Int,
) {
    companion object {
        fun createDtoFromPageable(scrapList: Page<ScrapPost>): MyScarpPostResponseDto {
            // 각각 scarplist의 원소의 post가 Normal인지 확인해서 map 연산 수행
            val postList = scrapList.content
                .filter { it.post.status == PostStatus.NORMAL }
                .map { it.post.toOneBasePostResponseDto() }

            return MyScarpPostResponseDto(
                contents = postList,
                currentPage = scrapList.pageable.pageNumber,
                totalPages = scrapList.totalPages,
                totalElements = scrapList.totalElements,
                numberOfElements = scrapList.numberOfElements,
                size = scrapList.size,
            )
        }
    }
}
