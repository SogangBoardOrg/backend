package dev.board.boardprojectproto.model

import jakarta.persistence.Entity

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
