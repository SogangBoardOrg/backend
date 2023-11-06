package com.kotlin.boardproject.domain.comment.dto

import com.kotlin.boardproject.domain.comment.domain.Comment
import com.kotlin.boardproject.domain.post.domain.NormalPost
import com.kotlin.boardproject.domain.user.domain.User

data class CommentsByPostIdResponseDto(
    val commentList: List<CommentDto>,
) {
    companion object {

        fun fromCommentList(
            post: NormalPost,
            searchUser: User?,
            commentList: List<Comment>,
        ): CommentsByPostIdResponseDto {
            return CommentsByPostIdResponseDto(
                commentList = if (!post.commentOn) emptyList() else commentDtos(post, commentList, searchUser),
            )
        }
    }
}
