package com.kotlin.boardproject.domain.post.repository

import com.kotlin.boardproject.domain.post.domain.BasePost
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.global.enums.PostStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface BasePostRepository : JpaRepository<BasePost, Long> {

    @Query(
        """
            SELECT DISTINCT p
            FROM BasePost AS p
            LEFT JOIN FETCH p.likeList
            WHERE p.id = :id AND p.status = :status
        """,
    )
    fun findByIdAndStatusFetchLikeList(id: Long, status: PostStatus): BasePost?

    @Query(
        """
            SELECT DISTINCT p
            FROM BasePost AS p
            LEFT JOIN FETCH p.commentList
            WHERE p.id = :id AND p.status = :status
        """,
    )
    fun findByIdAndStatusFetchCommentList(id: Long, status: PostStatus): BasePost?

    @Query(
        """
            SELECT DISTINCT p
            FROM BasePost AS p
            LEFT JOIN FETCH p.scrapList
            WHERE p.id = :id AND p.status = :status
        """,
    )
    fun findByIdAndStatusFetchScrapList(id: Long, status: PostStatus): BasePost?

    fun findByIdAndStatus(id: Long, status: PostStatus): BasePost?

    fun findByWriterAndStatusOrderByIdDesc(user: User, status: PostStatus, pageable: Pageable): Page<BasePost>
}