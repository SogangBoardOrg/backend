package com.kotlin.boardproject.domain.post.dto.normalpost

import com.kotlin.boardproject.domain.comment.dto.CommentDto
import com.kotlin.boardproject.domain.comment.dto.commentDtos
import com.kotlin.boardproject.domain.comment.domain.Comment
import com.kotlin.boardproject.domain.post.domain.NormalPost
import com.kotlin.boardproject.domain.user.domain.User
import java.time.LocalDateTime

data class OneNormalPostResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val writerName: String,
    val isAnon: Boolean,
    val isLiked: Boolean?,
    val isScrapped: Boolean?,
    val isWriter: Boolean?,
    val commentOn: Boolean,
    val commentCnt: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val commentList: List<CommentDto>,
    val photoList: List<String>,
) {
    companion object {
        fun fromNormalPost(
            post: NormalPost,
            searchUser: User?,
            commentList: List<Comment>,
        ): OneNormalPostResponseDto {
            return OneNormalPostResponseDto(
                id = post.id!!,
                commentOn = post.commentOn,
                title = post.title,
                isAnon = post.isAnon,
                content = post.title,
                isLiked = isLiked(post.likeList.map { it.user }, searchUser),
                isWriter = isWriter(post, searchUser),
                isScrapped = isScrapped(post, searchUser),
                writerName = postWriterNameGenerator(post),
                commentCnt = if (!post.commentOn) 0 else commentList.size,
                createdAt = post.createdAt!!,
                updatedAt = post.updatedAt!!,
                commentList = if (!post.commentOn) emptyList() else commentDtos(post, commentList, searchUser),
                photoList = post.photoList,
            )
        }

        private fun isLiked(userList: List<User>, searchUser: User?): Boolean =
            userList.contains(searchUser)

        private fun isScrapped(post: NormalPost, searchUser: User?): Boolean =
            post.scrapList.map { it.user }.contains(searchUser)

        private fun isWriter(post: NormalPost, searchUser: User?): Boolean =
            post.writer == searchUser

        private fun postWriterNameGenerator(post: NormalPost): String =
            if (post.isAnon) "ANON" else post.writer.nickname
    }
}
