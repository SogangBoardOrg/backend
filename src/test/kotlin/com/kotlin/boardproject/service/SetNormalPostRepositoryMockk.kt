package com.kotlin.boardproject.service

import com.kotlin.boardproject.domain.post.domain.NormalPost
import com.kotlin.boardproject.domain.post.repository.NormalPostRepository
import com.kotlin.boardproject.global.enums.PostStatus
import io.mockk.every

fun setNormalPostRepository(
    normalPostPresent: NormalPost,
    normalPostDeleted: NormalPost,
    normalPostRepository: NormalPostRepository,
) {
    every {
        normalPostRepository.findByIdAndStatus(
            normalPostPresent.id!!,
            PostStatus.NORMAL,
        )
    } returns normalPostPresent
    every {
        normalPostRepository.findByIdAndStatus(
            normalPostDeleted.id!!,
            PostStatus.NORMAL,
        )
    } returns null
    every {
        normalPostRepository.findByIdAndStatus(
            nonExistPostId,
            PostStatus.NORMAL,
        )
    } returns null
}
