package com.kotlin.boardproject.dto.comment

import com.kotlin.boardproject.common.enums.PostStatus
import com.kotlin.boardproject.model.*
import java.time.LocalDateTime

data class CommentDto(
    // TODO: 좋아요 여부와 좋아요 개수는 어떻게 처리할까?
    val id: Long,
    val parentId: Long?,
    val ancestorId: Long?,
    val likeCnt: Int,
    val content: String,
    val writerName: String,
    val isAnon: Boolean,
    val isLiked: Boolean,
    val isWriter: Boolean, // 로그인 한 사람이 댓글 작성자
    val isPostWriter: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val child: List<CommentDto> = listOf(),
) {
    companion object {
        fun fromEntity(
            comment: Comment,
            searchUser: User?,
            post: BasePost,
            writerMap: Map<User, Int>,
        ): CommentDto {
            return CommentDto(
                id = comment.id!!,
                content = commentContentGenerator(comment),
                isAnon = comment.isAnon,
                isLiked = isLiked(comment.likeList, searchUser),
                isWriter = isWriter(comment, searchUser),
                isPostWriter = isPostWriter(comment, post),
                writerName = commentWriterNameGenerator(comment, post, writerMap),
                createdAt = comment.createdAt!!,
                updatedAt = comment.updatedAt!!,
                ancestorId = comment.ancestor?.id,
                parentId = comment.parent?.id,
                likeCnt = comment.likeList.size,
                child = listOf(),
            )
        }

        private fun commentWriterNameGenerator(
            comment: Comment,
            post: BasePost,
            writerMap: Map<User, Int>,
        ): String =
            if (comment.writer == post.writer) {
                "글쓴이"
            } else if (comment.isAnon) {
                "익명 ${writerMap[comment.writer]}"
            } else {
                comment.writer.nickname
            }

        private fun isPostWriter(comment: Comment, post: BasePost): Boolean =
            comment.writer == post.writer

        private fun isWriter(comment: Comment, searchUser: User?): Boolean =
            searchUser?.let { comment.writer == searchUser } ?: false

        private fun isLiked(likeCommentList: List<LikeComment>, searchUser: User?): Boolean =
            likeCommentList.map { it.user }.contains(searchUser)

        private fun commentContentGenerator(comment: Comment): String =
            if (comment.status == PostStatus.DELETED) "삭제된 댓글입니다." else comment.content
    }
}
