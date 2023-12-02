package com.kotlin.boardproject.domain.post.domain

import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.global.enums.NormalType
import com.kotlin.boardproject.global.enums.PostStatus
import com.kotlin.boardproject.global.enums.PostType
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
    postType = PostType.NORMAL,
    photoList = photoList,
)
