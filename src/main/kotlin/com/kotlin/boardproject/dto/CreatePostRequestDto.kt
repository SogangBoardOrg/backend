package com.kotlin.boardproject.dto

import com.kotlin.boardproject.model.BasePost
import com.kotlin.boardproject.model.User

data class CreatePostRequestDto(
    val title: String,
    val content: String,
    val isAnon: Boolean,
    val commentOn: Boolean,
){
    fun toPost(user: User): BasePost {
        return BasePost(
            title = title,
            content = content,
            writer = user,
            isAnon = isAnon,
            commentOn = commentOn,
        )
    }
}
