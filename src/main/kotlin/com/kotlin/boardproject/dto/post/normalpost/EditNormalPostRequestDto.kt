package com.kotlin.boardproject.dto.post.normalpost

class EditNormalPostRequestDto(
    val title: String,
    val content: String,
    val isAnon: Boolean,
    val commentOn: Boolean,
    val photoList: List<String> = emptyList(),
)
