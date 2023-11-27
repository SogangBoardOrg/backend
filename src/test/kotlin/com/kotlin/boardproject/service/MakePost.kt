package com.kotlin.boardproject.service

import com.kotlin.boardproject.domain.post.domain.NormalPost
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.global.enums.NormalType
import com.kotlin.boardproject.global.enums.PostStatus

fun makeNormalPost(writer: User): List<NormalPost> {
    val one = NormalPost(
        title = "postOne",
        content = "postOne",
        writer = writer,
        isAnon = false,
        commentOn = true,
        normalType = NormalType.FREE,
    )
    val two = NormalPost(
        title = "postOne",
        content = "postOne",
        writer = writer,
        isAnon = false,
        commentOn = true,
        normalType = NormalType.FREE,
    )

    one.id = 1L
    two.id = 2L
    two.status = PostStatus.DELETED
    return listOf(one, two)
}
