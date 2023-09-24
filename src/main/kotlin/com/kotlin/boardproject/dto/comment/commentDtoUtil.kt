package com.kotlin.boardproject.dto.comment

import com.kotlin.boardproject.model.Comment
import com.kotlin.boardproject.model.NormalPost
import com.kotlin.boardproject.model.User

fun commentDtos(
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
