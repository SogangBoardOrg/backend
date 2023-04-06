package com.kotlin.boardproject.repository

import com.kotlin.boardproject.common.enums.NormalType
import com.kotlin.boardproject.common.enums.PostStatus
import com.kotlin.boardproject.model.NormalPost
import com.kotlin.boardproject.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface NormalPostRepository : JpaRepository<NormalPost, Long> {
    fun findByIdAndStatus(id: Long, status: PostStatus): NormalPost?

    @Query(
        "SELECT np FROM NormalPost AS np WHERE ((np.writer = :writer AND np.isAnon = FALSE) OR :writer IS NULL )" +
            "AND (np.title LIKE '%' || :title || '%' OR :title IS NULL)" +
            "AND (np.content LIKE '%' || :content || '%' OR :content IS NULL)" +
            "AND (np.normalType = :normalType)",
    )
    fun findByQuery(
        @Param("title") title: String?,
        @Param("content") content: String?,
        @Param("writer") writer: User?,
        @Param("normalType") normalType: NormalType,
        pageable: Pageable,
    ): Page<NormalPost>
}
