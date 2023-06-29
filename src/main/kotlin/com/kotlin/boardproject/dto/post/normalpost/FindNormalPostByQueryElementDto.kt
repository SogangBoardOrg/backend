package com.kotlin.boardproject.dto.post.normalpost

import com.kotlin.boardproject.model.NormalPost
import com.kotlin.boardproject.model.User
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
data class FindNormalPostByQueryElementDto @QueryProjection constructor(
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
        ): FindNormalPostByQueryElementDto {
            return FindNormalPostByQueryElementDto(
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
