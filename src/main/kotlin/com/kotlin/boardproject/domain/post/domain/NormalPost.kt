package com.kotlin.boardproject.domain.post.domain

import com.kotlin.boardproject.domain.post.dto.normalpost.EditNormalPostRequestDto
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.global.enums.ErrorCode
import com.kotlin.boardproject.global.enums.NormalType
import com.kotlin.boardproject.global.enums.PostStatus
import com.kotlin.boardproject.global.exception.ConditionConflictException
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
    photoList: List<String> = emptyList(),
    @Enumerated(EnumType.STRING)
    val normalType: NormalType,
) : BasePost(
    title = title,
    content = content,
    isAnon = isAnon,
    commentOn = commentOn,
    writer = writer,
    status = PostStatus.NORMAL,
    photoList = photoList,
) {
    fun editPost(editNormalPostRequestDto: EditNormalPostRequestDto) {
        // TODO:질문 글이면 수정 불가능하게 만들기

        this.title = editNormalPostRequestDto.title
        this.isAnon = editNormalPostRequestDto.isAnon
        this.commentOn = editNormalPostRequestDto.commentOn
        this.content = editNormalPostRequestDto.content
        this.photoList = editNormalPostRequestDto.photoList
    }

    fun notQuestion() {
        require(this.normalType != NormalType.QUESTION) {
            throw ConditionConflictException(ErrorCode.FORBIDDEN, "질문 글은 삭제할 수 없습니다.")
        }
    }
}
