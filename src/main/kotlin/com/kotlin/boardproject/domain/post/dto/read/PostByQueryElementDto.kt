package com.kotlin.boardproject.domain.post.dto.read

import com.kotlin.boardproject.global.enums.PostType
import com.kotlin.boardproject.global.enums.Seasons
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class PostByQueryElementDto @QueryProjection constructor(
    val id: Long,
    val title: String,
    val content: String,
    val writerName: String,
    val writerProfileImageUrl: String?,
    val isAnon: Boolean,
    val isLiked: Boolean,
    val isScrapped: Boolean,
    val isWriter: Boolean,
    val postType: PostType,
    val courseId: Long?,
    val courseYear: Int?,
    val courseSeason: Seasons?,
    val courseCode: String?,
    val courseName: String?,
    val reviewScore: Int?,
    val commentOn: Boolean,
    val commentCnt: Int,
    val likeCnt: Int,
    val scrapCnt: Int,
    val photoCnt: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
