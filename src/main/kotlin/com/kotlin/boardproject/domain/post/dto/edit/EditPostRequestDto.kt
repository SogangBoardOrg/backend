package com.kotlin.boardproject.domain.post.dto.edit

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class EditPostRequestDto(
    @field: NotBlank
    val title: String,
    @field: NotBlank
    val content: String,
    @field: NotNull
    val isAnon: Boolean,
    @field: NotNull
    val commentOn: Boolean,
    val photoList: List<String>,
)
