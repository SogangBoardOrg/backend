package com.kotlin.boardproject.domain.post.dto.normalpost

import com.kotlin.boardproject.domain.post.domain.NormalPost
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.global.enums.NormalType
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CreateNormalPostRequestDto(
    @field: NotBlank
    val title: String,
    @field: NotBlank
    val content: String,
    @field: NotNull
    val isAnon: Boolean,
    @field: NotNull
    val commentOn: Boolean,
    @field: NotNull
    val normalType: NormalType,
    @field: Valid
    val photoList: List<@NotBlank String>,
) {
    fun toPost(user: User): NormalPost {
        return NormalPost(
            title = title,
            content = content,
            writer = user,
            isAnon = isAnon,
            commentOn = commentOn,
            normalType = normalType,
            photoList = photoList,
        )
    }
}
