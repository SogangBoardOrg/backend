package com.kotlin.boardproject.model

import com.kotlin.boardproject.common.enums.PostStautus
import javax.persistence.Entity

@Entity
class FreePost(
    title: String,
    content: String,
    isAnon: Boolean,
    commentOn: Boolean,
    writer: User,
) : BasePost(
    title = title,
    content = content,
    isAnon = isAnon,
    commentOn = commentOn,
    writer = writer,
    status = PostStautus.NORMAL
)
