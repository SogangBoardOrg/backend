package com.kotlin.boardproject.service

import com.kotlin.boardproject.domain.post.domain.BasePost
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.global.enums.PostStatus
import com.kotlin.boardproject.global.enums.PostType

fun makeFreePost(writer: User): List<BasePost> {
    val one = BasePost(
        title = "postOne",
        content = "postOne",
        writer = writer,
        isAnon = false,
        commentOn = true,
        postType = PostType.FREE,
    )
    val two = BasePost(
        title = "postOne",
        content = "postOne",
        writer = writer,
        isAnon = false,
        commentOn = true,
        postType = PostType.FREE,
    )

    one.id = 1L
    two.id = 2L
    two.status = PostStatus.DELETED
    return listOf(one, two)
}
