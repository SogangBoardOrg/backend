package com.kotlin.boardproject.domain.post.dto.normalpost

import com.kotlin.boardproject.domain.post.domain.NormalPost
import com.kotlin.boardproject.domain.user.domain.User
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

// writer fetch join
// scrapList,
// photoList -> cnt,
// commentList -> cnt
// likeList,
// fetch join
// post 가져오기
// select 에 scrapList, photoList가져오기, likeList 가져오기
data class NormalPostByQueryElementDto @QueryProjection constructor(
    val id: Long,
    val title: String,
    val content: String,
    val writerName: String,
    val isAnon: Boolean,
    val isLiked: Boolean,
    val isScrapped: Boolean,
    val isWriter: Boolean,
    val commentOn: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val commentCnt: Int,
    val likeCnt: Int,
    val scrapCnt: Int,
    val photoCnt: Int,
) {
    companion object {
        fun fromNormalPostToQueryOneNormalPostResponseDto(
            post: NormalPost,
            user: User?,
        ): NormalPostByQueryElementDto {
            return NormalPostByQueryElementDto(
                id = post.id!!,
                title = post.title,
                content = post.content,
                writerName = if (post.isAnon) "Anon" else post.writer.nickname,
                isAnon = post.isAnon,
                isLiked = (user?.likePostList?.map { it.post }?.contains(post) ?: false),
                isScrapped = (user?.scrapList?.map { it.post }?.contains(post) ?: false),
                isWriter = (user == post.writer),
                commentOn = post.commentOn,
                createdAt = post.createdAt!!,
                updatedAt = post.updatedAt!!,
                commentCnt = post.commentList.size,
                likeCnt = post.likeList.size,
                scrapCnt = post.scrapList.size,
                photoCnt = post.photoList.size,
            )
        }
    }
}