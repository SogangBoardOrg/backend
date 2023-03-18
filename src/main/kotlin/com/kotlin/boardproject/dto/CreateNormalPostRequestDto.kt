package com.kotlin.boardproject.dto

import com.kotlin.boardproject.common.enums.CurrentStatus
import com.kotlin.boardproject.common.enums.FreeType
import com.kotlin.boardproject.model.BasePost
import com.kotlin.boardproject.model.NormalPost
import com.kotlin.boardproject.model.User

data class CreateNormalPostRequestDto(
    val title: String,
    val content: String,
    val isAnon: Boolean,
    val commentOn: Boolean,
    val freeType: FreeType
){
    fun toPost(user: User): NormalPost {
        return NormalPost(
            title = title,
            content = content,
            writer = user,
            isAnon = isAnon,
            commentOn = commentOn,
            freeType = freeType,
        )
    }
}
