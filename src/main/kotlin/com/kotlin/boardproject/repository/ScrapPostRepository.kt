package com.kotlin.boardproject.repository

import com.kotlin.boardproject.common.enums.PostStatus
import com.kotlin.boardproject.model.BasePost
import com.kotlin.boardproject.model.ScrapPost
import com.kotlin.boardproject.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ScrapPostRepository : JpaRepository<ScrapPost, Long> {
    fun findByUserAndPost(user: User, post: BasePost): ScrapPost?

    @Query(
        """
        SELECT DISTINCT s
        FROM ScrapPost AS s
        LEFT JOIN FETCH s.post
        WHERE s.user = :user AND s.post.status = :postStatus
        order by s.id DESC 
    """,
        countQuery = """
        SELECT COUNT(s)
        FROM ScrapPost AS s
        WHERE s.user = :user AND s.post.status = :postStatus
    """,
    )
    fun findByWriterAndStatusOrderByIdDesc(user: User, postStatus: PostStatus, pageable: Pageable): Page<ScrapPost>
}
