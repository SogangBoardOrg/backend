package com.kotlin.boardproject.domain.comment.repository

import com.kotlin.boardproject.domain.comment.domain.Comment
import com.kotlin.boardproject.domain.post.domain.BasePost
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.global.enums.PostStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CommentRepository : JpaRepository<Comment, Long> {

    @Query(
        """
            SELECT DISTINCT c
            FROM Comment c
            LEFT JOIN FETCH c.ancestor
            LEFT JOIN FETCH c.post
            WHERE c.id = :id AND c.status = :status
        """,
    )
    fun findByIdAndStatusFetchAncestorAndPost(id: Long, status: PostStatus): Comment?

    @Query(
        """
            SELECT DISTINCT c
            FROM Comment c
            LEFT JOIN FETCH c.likeList
            WHERE c.id = :id AND c.status = :status
        """,
    )
    fun findByIdAndStatusFetchLikeList(id: Long, status: PostStatus): Comment?

    @Query(
        """
            SELECT c
            FROM Comment c
            LEFT JOIN FETCH c.post
            WHERE c.id = :id AND c.status = :status
        """,
    )
    fun findByIdAndStatusFetchPost(id: Long, status: PostStatus): Comment?

    fun findByIdAndStatus(id: Long, status: PostStatus): Comment?

    @Query(
        """SELECT DISTINCT c
            FROM Comment c
            LEFT JOIN FETCH c.likeList cl
            LEFT JOIN FETCH cl.user
            LEFT JOIN FETCH c.writer
            LEFT JOIN FETCH c.parent
            LEFT JOIN FETCH c.ancestor
            WHERE c.post = :post
            ORDER BY c.id ASC """,
    )
    fun findByPostFetchLikeListOrderById(post: BasePost): List<Comment>

    @Query(
        """
            SELECT c
            FROM Comment c
            LEFT JOIN FETCH c.post
            WHERE c.writer = :user AND c.status = :status
            ORDER BY c.id DESC
        """,
        countQuery = """
            SELECT COUNT(c)
            FROM Comment c
            WHERE c.writer = :user AND c.status = :status
            ORDER BY c.id DESC
        """,
    )
    fun findByWriterAndStatusFetchPost(user: User, status: PostStatus, pageable: Pageable): Page<Comment>
}
