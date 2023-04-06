package com.kotlin.boardproject.model

import com.kotlin.boardproject.common.enums.NormalType
import com.kotlin.boardproject.common.enums.PostStatus
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
        isLiked: Boolean? = null,
        isWriter: Boolean? = null,
        isScrapped: Boolean? = null,
    ): OneNormalPostResponseDto {
        // TODO: 이렇게 받는거 말고 다른거 없음?
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
        )
    }
}
