package com.kotlin.boardproject.domain.post.dto.read

import com.kotlin.boardproject.domain.comment.domain.Comment
import com.kotlin.boardproject.domain.comment.dto.read.CommentDto
import com.kotlin.boardproject.domain.comment.dto.read.commentDtos
import com.kotlin.boardproject.domain.post.domain.BasePost
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.global.enums.PostType
import java.time.LocalDateTime

data class OnePostResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val writerName: String,
    val writerProfileImageUrl: String?,
    val isAnon: Boolean,
    val isLiked: Boolean?,
    val isScrapped: Boolean?,
    val isWriter: Boolean?,
    val commentOn: Boolean,
    val commentCnt: Int,
    val likeCnt: Int,
    val reviewScore: Int?,
    val postType: PostType,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val commentList: List<CommentDto>,
    val photoList: List<String>,
) {
    companion object {
        fun fromPost(
            post: BasePost,
            searchUser: User?,
            commentList: List<Comment>,
        ): OnePostResponseDto {
            return OnePostResponseDto(
                id = post.id!!,
                commentOn = post.commentOn,
                title = post.title,
                isAnon = post.isAnon,
                content = post.content,
                isLiked = isLiked(post.likeList.map { it.user }, searchUser),
                isWriter = isWriter(post, searchUser),
                isScrapped = isScrapped(post, searchUser),
                writerName = postWriterNameGenerator(post),
                writerProfileImageUrl = postWriterProfileImageGenerator(post),
                commentCnt = if (!post.commentOn) 0 else commentList.size,
                likeCnt = post.likeList.size,
                createdAt = post.createdAt!!,
                updatedAt = post.updatedAt!!,
                commentList = if (!post.commentOn) emptyList() else commentDtos(post, commentList, searchUser),
                photoList = post.photoList,
                reviewScore = post.reviewScore,
                postType = post.postType,
            )
        }

        private fun isLiked(userList: List<User>, searchUser: User?): Boolean =
            userList.contains(searchUser)

        private fun isScrapped(post: BasePost, searchUser: User?): Boolean =
            post.scrapList.map { it.user }.contains(searchUser)

        private fun isWriter(post: BasePost, searchUser: User?): Boolean =
            post.writer == searchUser

        private fun postWriterNameGenerator(post: BasePost): String =
            if (post.isAnon) "ANON" else post.writer.nickname

        private fun postWriterProfileImageGenerator(post: BasePost): String? =
            if (post.isAnon) null else post.writer.profileImageUrl
    }
}
