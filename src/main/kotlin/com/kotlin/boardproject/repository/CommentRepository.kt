package com.kotlin.boardproject.repository

import com.kotlin.boardproject.common.enums.PostStatus
import com.kotlin.boardproject.model.BasePost
import com.kotlin.boardproject.model.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByIdAndStatus(id: Long, status: PostStatus): Comment?

    @Query(
        "SELECT c " +
            "FROM Comment c " +
            "LEFT JOIN FETCH c.likeList " +
            "WHERE c.post = :post",
    )
    fun findByPost(post: BasePost): List<Comment>
}
