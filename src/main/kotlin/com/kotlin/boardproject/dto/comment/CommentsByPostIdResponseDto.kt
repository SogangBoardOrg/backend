package com.kotlin.boardproject.dto.comment

import com.kotlin.boardproject.model.Comment
import com.kotlin.boardproject.model.NormalPost
import com.kotlin.boardproject.model.User

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
