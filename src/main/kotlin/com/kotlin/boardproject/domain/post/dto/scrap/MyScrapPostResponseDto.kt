package com.kotlin.boardproject.domain.post.dto.scrap

import com.kotlin.boardproject.domain.post.domain.ScrapPost
import com.kotlin.boardproject.domain.post.dto.read.MyWrittenPostResponseElementDto
import com.kotlin.boardproject.global.enums.PostStatus
import org.springframework.data.domain.Page

data class MyScrapPostResponseDto(
    val contents: List<MyWrittenPostResponseElementDto>? = null,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val numberOfElements: Int,
    val size: Int,
) {
    companion object {
        fun createDtoFromPageable(scrapList: Page<ScrapPost>): MyScrapPostResponseDto {
            // 각각 scarplist의 원소의 post가 Normal인지 확인해서 map 연산 수행
            return MyScrapPostResponseDto(
                contents = generatePostDtoList(scrapList),
                currentPage = scrapList.pageable.pageNumber,
                totalPages = scrapList.totalPages,
                totalElements = scrapList.totalElements,
                numberOfElements = scrapList.numberOfElements,
                size = scrapList.size,
            )
        }

        private fun generatePostDtoList(scrapList: Page<ScrapPost>): List<MyWrittenPostResponseElementDto> =
            scrapList.content
                .filter { it.post.status == PostStatus.NORMAL }
                .map { MyWrittenPostResponseElementDto.fromBasePostToDto(it.post) }
    }
}
