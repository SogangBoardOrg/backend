package com.kotlin.boardproject.service

import com.kotlin.boardproject.domain.post.domain.BasePost
import com.kotlin.boardproject.domain.post.repository.BasePostRepository
import com.kotlin.boardproject.global.enums.PostStatus
import io.mockk.every

fun setBasePostRepository(
    freePostPresent: BasePost,
    freePostDeleted: BasePost,
    basePostRepository: BasePostRepository,
) {
    every {
        basePostRepository.findByIdAndStatus(
            freePostPresent.id!!,
            PostStatus.NORMAL,
        )
    } returns freePostPresent
    every {
        basePostRepository.findByIdAndStatusFetchScrapList(
            freePostPresent.id!!,
            PostStatus.NORMAL,
        )
    } returns freePostPresent

    every {
        basePostRepository.findByIdAndStatus(
            freePostDeleted.id!!,
            PostStatus.NORMAL,
        )
    } returns null
    every {
        basePostRepository.findByIdAndStatusFetchScrapList(
            freePostDeleted.id!!,
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
        basePostRepository.findByIdAndStatusFetchScrapList(
            nonExistPostId,
            PostStatus.NORMAL,
        )
    } returns null
}
