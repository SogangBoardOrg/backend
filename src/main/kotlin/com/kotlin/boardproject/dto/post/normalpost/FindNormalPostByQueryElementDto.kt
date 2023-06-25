package com.kotlin.boardproject.dto.post.normalpost

import java.time.LocalDateTime

data class FindNormalPostByQueryElementDto(
    val id: Long,
    val title: String,
    val content: String,
    val writerName: String,
    val isAnon: Boolean,
    val isLiked: Boolean?,
    val isScrapped: Boolean?,
    val isWriter: Boolean?,
    val commentOn: Boolean,
    val createdTime: LocalDateTime,
    val lastModifiedTime: LocalDateTime?,
    val commentCnt: Int,
    val likeCnt: Int,
    val scrapCnt: Int,
    val photoCnt: Int,
) {
    companion object {
//        fun fromNormalPostToQueryOneNormalPostResponseDto(normalPost: NormalPost, user: User?): QueryOneNormalPostResponseDto {
//            return QueryOneNormalPostResponseDto(
//                id = normalPost.id!!,
//                title = normalPost.title,
//                content = normalPost.content,
//                writerName = normalPost.writer.nickname,
//                isAnon = normalPost.isAnon,
//                //isLiked = normalPost.isLiked(user),
//                //isScrapped = normalPost.isScrapped(user),
//                //isWriter = normalPost.isWriter(user),
//                commentOn = normalPost.commentOn,
//                //createdTime = normalPost.createdTime,
//                //lastModifiedTime = normalPost.lastModifiedTime,
//                //commentCnt = normalPost.commentCnt,
//                //likeCnt = normalPost.likeCnt,
//                //scrapCnt = normalPost.scrapCnt,
//                //photoCnt = normalPost.photoCnt,
//            ) //
        // }
    }
}
