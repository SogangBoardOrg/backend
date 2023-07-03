package com.kotlin.boardproject.dto.post.normalpost

import com.kotlin.boardproject.dto.comment.CommentDto
import com.kotlin.boardproject.model.Comment
import com.kotlin.boardproject.model.NormalPost
import com.kotlin.boardproject.model.User
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

        private fun commentDtos(
            post: NormalPost,
            commentList: List<Comment>,
            searchUser: User?,
        ): List<CommentDto> {
            // 1. 댓글 전체 수색
            // 2. 글쓴이는 리스트의 1번째에 넣어준다.
            // 3. 새로운 댓글 작성자일 때 마다 리스트에 넣어준다. -> N
            // 4. 댓글을 순회하면서 parent와 ancestor가 null인 댓글은 ancestor list로 배치한다.
            // 5. 댓글을 순회하면서 parent와 ancestor가 null이 아닌 댓글은 ancestor를 찾아서 해당 ancestor의 child 리스트 안에 넣어줌
            // ** 모든 과정에서 이 함수로 들어온 모든 commentlist의 순서는 보존이 되어야한다. **
            // ancestorlist를 만들 때도 commentlist내부의 순서를 따른다.

            // commentList는 id순으로 정렬 되어있다.

            val writerMap: Map<User, Int> = generateWriterMap(post, commentList)
            val commentDtoList: List<CommentDto> = convertToCommentDtoList(post, searchUser, commentList, writerMap)
            return returnList(commentDtoList)
        }

        private fun returnList(
            commentDtoList: List<CommentDto>,
        ): List<CommentDto> {
            // 4. 댓글을 순회하면서 parent와 ancestor가 null인 댓글은 ancestor list로 배치한다.
            // 5. 댓글을 순회하면서 parent와 ancestor가 null이 아닌 댓글은 ancestor를 찾아서 해당 ancestor의 child 리스트 안에 넣어줌
            val (ancestors, children) = commentDtoList.partition { it.parentId == null && it.ancestorId == null }

            return ancestors.map { ancestor ->
                ancestor.copy(
                    child = children.filter {
                        it.ancestorId == ancestor.id
                    },
                )
            }
        }

        private fun convertToCommentDtoList(
            post: NormalPost,
            searchUser: User?,
            commentList: List<Comment>,
            writerMap: Map<User, Int>,
        ): List<CommentDto> {
            return commentList.map {
                CommentDto.fromEntity(it, searchUser, post, writerMap)
            }
        }

        private fun generateWriterMap(
            post: NormalPost,
            commentList: List<Comment>,
        ): Map<User, Int> {
            // 2. 글쓴이는 리스트의 1번째에 넣어준다.
            // 3. 새로운 댓글 작성자일 때 마다 리스트에 넣어준다. -> N
            val writerMap = commentList
                .filter { it.writer != post.writer }
                .map { it.writer }
                .distinct()
                .withIndex()
                .associate { (index, writer) -> writer to index + 1 }
                .toMutableMap()

            writerMap[post.writer] = 0

            // key를 writer로 하고 value를 익명번호로 하는 map을 만든다.

            return writerMap.toMap()
        }

        private fun isLiked(userList: List<User>, searchUser: User?): Boolean = userList.contains(searchUser)

        private fun isScrapped(post: NormalPost, searchUser: User?): Boolean =
            post.scrapList.map { it.user }.contains(searchUser)

        private fun isWriter(post: NormalPost, searchUser: User?): Boolean = post.writer == searchUser

        private fun postWriterNameGenerator(post: NormalPost): String =
            if (post.isAnon) "ANON" else post.writer.nickname
    }
}
