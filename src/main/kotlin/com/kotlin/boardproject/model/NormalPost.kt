package com.kotlin.boardproject.model

import com.kotlin.boardproject.common.enums.ErrorCode
import com.kotlin.boardproject.common.enums.NormalType
import com.kotlin.boardproject.common.enums.PostStautus
import com.kotlin.boardproject.common.exception.UnAuthorizedException
import com.kotlin.boardproject.dto.EditNormalPostRequestDto
import com.kotlin.boardproject.dto.OneNormalPostResponseDto
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
    status = PostStautus.NORMAL,
) {
    fun editPost(editNormalPostRequestDto: EditNormalPostRequestDto) {
        // TODO:질문 글이면 수정 불가능하게 만들기

        this.title = editNormalPostRequestDto.title
        this.isAnon = editNormalPostRequestDto.isAnon
        this.commentOn = editNormalPostRequestDto.commentOn
        this.content = editNormalPostRequestDto.content
    }

    fun findWriter(user: User) {
        if (user != this.writer) {
            throw UnAuthorizedException(ErrorCode.FORBIDDEN, "해당 글의 주인이 아닙니다.")
        }
    }

    fun toOneNormalPostReponseDto(): OneNormalPostResponseDto {
        return OneNormalPostResponseDto(
            id = this.id!!,
            commentOn = this.commentOn,
            title = this.title,
            isAnon = this.isAnon,
            content = this.title,
            writerName = if (this.isAnon) "Anon" else this.writer.username,
            createTime = this.createdAt!!,
            lastModifiedTime = this.updatedAt,
        )
    }
}
