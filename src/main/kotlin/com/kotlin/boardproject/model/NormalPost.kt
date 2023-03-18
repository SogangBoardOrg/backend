package com.kotlin.boardproject.model

import com.kotlin.boardproject.common.enums.FreeType
import com.kotlin.boardproject.common.enums.PostStautus
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

    @Enumerated(EnumType.STRING)
    val freeType: FreeType,
) : BasePost(
    title = title,
    content = content,
    isAnon = isAnon,
    commentOn = commentOn,
    writer = writer,
    status = PostStautus.NORMAL
)
