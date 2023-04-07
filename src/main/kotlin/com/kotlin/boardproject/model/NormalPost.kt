package com.kotlin.boardproject.model

import com.kotlin.boardproject.common.enums.NormalType
import com.kotlin.boardproject.common.enums.PostStatus
import com.kotlin.boardproject.dto.comment.CommentDto
import com.kotlin.boardproject.dto.post.normalpost.EditNormalPostRequestDto
import com.kotlin.boardproject.dto.post.normalpost.OneNormalPostResponseDto
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity
class NormalPost(
    title: String,
    content: String,
    isAnon: Boolean,
    commentOn: Boolean,
    writer: User,

    @Enumerated(EnumType.STRING)
    val normalType: NormalType,
) : BasePost(
    title = title,
    content = content,
    isAnon = isAnon,
    commentOn = commentOn,
    writer = writer,
    status = PostStatus.NORMAL,
) {
    fun editPost(editNormalPostRequestDto: EditNormalPostRequestDto) {
        // TODO:질문 글이면 수정 불가능하게 만들기

        this.title = editNormalPostRequestDto.title
        this.isAnon = editNormalPostRequestDto.isAnon
        this.commentOn = editNormalPostRequestDto.commentOn
        this.content = editNormalPostRequestDto.content
    }

    fun toOneNormalPostResponseDto(
        user: User? = null,
    ): OneNormalPostResponseDto {
        val isWriter = user?.let { this.writer == user } ?: false
        val isLiked = this.likeList.find { it.user == this.writer }?.let { true } ?: false
        val isScrapped = user?.let { this.scrapList.find { it.user == user }?.let { true } ?: false } ?: false

        // 1. 댓글 전체 수색
        // 2. 글쓴이는 리스트의 1번째에 넣어준다.
        // 3. 새로운 댓글 작성자일 때 마다 리스트에 넣어준다. -> N
        // 4. 댓글을 순회하면서 parent와 ancestor가 null인 댓글은 맨 처음으로 배치한다.
        // 5. 댓글을 순회하면서 parent와 ancestor가 null이 아닌 댓글은 ancestor를 찾아서 해당 리스트 안에 투입

        // 익명 번호 메기기
        val writerList: MutableList<User> = mutableListOf()
        writerList.add(this.writer)
        for (i in this.commentList) {
            if (writerList.find { it == i.writer } == null) {
                writerList.add(i.writer)
            }
        }

        val commentDtoList: MutableList<CommentDto> = this.commentList.map {
            CommentDto(
                id = it.id!!,
                content = it.content,
                isAnon = it.isAnon,
                // isLiked = it.likeList.find { it.user == user }?.let { true } ?: false,
                isWriter = it.writer == user,
                writerName = if (it.writer == this.writer) "글쓴이" else if (it.isAnon) "익명 ${writerList.indexOf(it.writer)}" else it.writer.nickname,
                createdTime = it.createdAt!!,
                lastModifiedTime = it.updatedAt,
                ancestorId = it.ancestor?.id,
                parentId = it.parent?.id,
                child = mutableListOf<CommentDto>(),
            )
        }.toMutableList()
        commentDtoList.sortBy { it.id }

        val ancestorList: MutableList<CommentDto> = mutableListOf()

        for (i in commentDtoList) {
            if (i.ancestorId == null && i.parentId == null) {
                ancestorList.add(i)
            }
        }
        ancestorList.sortBy { it.id }

        for (i in commentDtoList) {
            if (i.ancestorId != null && i.parentId != null) {
                val ancestor = ancestorList.find { it.id == i.ancestorId }
                ancestor?.child?.add(i)
            }
        }
        ancestorList.map {
            it.child.sortedBy { c -> c.id }
        }

        return OneNormalPostResponseDto(
            id = this.id!!,
            commentOn = this.commentOn,
            title = this.title,
            isAnon = this.isAnon,
            content = this.title,
            isLiked = isLiked,
            isWriter = isWriter,
            isScrapped = isScrapped,
            writerName = if (this.isAnon) "Anon" else this.writer.nickname,
            createdTime = this.createdAt!!,
            lastModifiedTime = this.updatedAt,
            commentList = if (!commentOn) mutableListOf() else ancestorList,
        )
    }
}
