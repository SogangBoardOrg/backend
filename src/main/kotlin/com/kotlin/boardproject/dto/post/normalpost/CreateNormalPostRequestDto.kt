package com.kotlin.boardproject.dto.post.normalpost

import com.kotlin.boardproject.common.enums.NormalType
import com.kotlin.boardproject.model.NormalPost
import com.kotlin.boardproject.model.User

data class CreateNormalPostRequestDto(
    val title: String,
    val content: String,
    val isAnon: Boolean,
    val commentOn: Boolean,
    val normalType: NormalType
){
    fun toPost(user: User): NormalPost {
        return NormalPost(
            title = title,
            content = content,
            writer = user,
            isAnon = isAnon,
            commentOn = commentOn,
            normalType = normalType,
        )
    }
}
