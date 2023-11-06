package com.kotlin.boardproject.domain.post.repository

import com.kotlin.boardproject.domain.post.domain.NormalPost
import com.kotlin.boardproject.global.enums.PostStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface NormalPostRepository : JpaRepository<NormalPost, Long>, NormalPostRepositoryCustom {

    // 작성자 여부
    // 좋아요 여부
    // 스크랩 여부

    fun findByIdAndStatus(id: Long, status: PostStatus): NormalPost?

    @Query(
        """
            SELECT DISTINCT np
            FROM NormalPost AS np
            LEFT JOIN FETCH np.likeList
            WHERE np.id = :id AND np.status = :status""",
    )
    fun findByIdAndStatusFetchLikeList(
        @Param("id") id: Long,
        @Param("status") status: PostStatus,
    ): NormalPost?

    @Query(
        """
            SELECT DISTINCT np
            FROM NormalPost AS np
            LEFT JOIN FETCH np.photoList
            LEFT JOIN FETCH np.writer
            WHERE np.id = :id AND np.status = :status""",
    )
    fun findByIdAndStatusFetchPhotoListAndUser(
        @Param("id") id: Long,
        @Param("status") status: PostStatus,
    ): NormalPost?

    @Query(
        """
            SELECT DISTINCT np
            FROM NormalPost AS np
            LEFT JOIN FETCH np.scrapList
            WHERE np.id = :id AND np.status = :status""",
    )
    fun findByIdAndStatusFetchScrapList(
        @Param("id") id: Long,
        @Param("status") status: PostStatus,
    ): NormalPost?
}
