package com.kotlin.boardproject.dto

class EditPostRequestDto(
    val title: String,
    val content: String,
    val isAnon: Boolean,
    val commentOn: Boolean,
)
