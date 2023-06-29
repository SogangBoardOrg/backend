package com.kotlin.boardproject.dto.post.normalpost

import com.kotlin.boardproject.common.enums.NormalType
import com.kotlin.boardproject.model.NormalPost
import com.kotlin.boardproject.model.User
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
    val photoList: List<String>,
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
