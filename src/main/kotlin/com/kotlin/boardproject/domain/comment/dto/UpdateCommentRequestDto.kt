package com.kotlin.boardproject.domain.comment.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

data class UpdateCommentRequestDto(
    @field:NotBlank
    val content: String,
    @field: NotNull
    val isAnon: Boolean,
    @field:Positive
    val postId: Long,
)
