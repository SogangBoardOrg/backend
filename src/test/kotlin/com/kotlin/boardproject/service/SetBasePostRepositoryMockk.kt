package com.kotlin.boardproject.service

import com.kotlin.boardproject.domain.post.domain.BasePost
import com.kotlin.boardproject.domain.post.repository.BasePostRepository
import com.kotlin.boardproject.global.enums.PostStatus
import io.mockk.every

fun setBasePostRepository(
    normalPostPresent: BasePost,
    normalPostDeleted: BasePost,
    basePostRepository: BasePostRepository,
) {
    every {
        basePostRepository.findByIdAndStatus(
            normalPostPresent.id!!,
            PostStatus.NORMAL,
        )
    } returns normalPostPresent
    every {
        basePostRepository.findByIdAndStatusFetchLikeList(
            normalPostPresent.id!!,
            PostStatus.NORMAL,
        )
    } returns normalPostPresent
    every {
        basePostRepository.findByIdAndStatusFetchScrapList(
            normalPostPresent.id!!,
            PostStatus.NORMAL,
        )
    } returns normalPostPresent

    every {
        basePostRepository.findByIdAndStatus(
            normalPostDeleted.id!!,
            PostStatus.NORMAL,
        )
    } returns null
    every {
        basePostRepository.findByIdAndStatusFetchLikeList(
            normalPostDeleted.id!!,
            PostStatus.NORMAL,
        )
    } returns null
    every {
        basePostRepository.findByIdAndStatusFetchScrapList(
            normalPostDeleted.id!!,
            PostStatus.NORMAL,
        )
    } returns null

    every {
        basePostRepository.findByIdAndStatus(
            nonExistPostId,
            PostStatus.NORMAL,
        )
    } returns null
    every {
        basePostRepository.findByIdAndStatusFetchLikeList(
            nonExistPostId,
            PostStatus.NORMAL,
        )
    } returns null
    every {
        basePostRepository.findByIdAndStatusFetchScrapList(
            nonExistPostId,
            PostStatus.NORMAL,
        )
    } returns null
}
