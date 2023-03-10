package com.kotlin.boardproject.model

import javax.persistence.Entity


@Entity
class FreePost(
    title: String,
    content: String,
    isAnon: Boolean,
    commentOn: Boolean,

    // head
) : BasePost(
    title = title,
    content = content,
    isAnon = isAnon,
    commentOn = commentOn,
)
