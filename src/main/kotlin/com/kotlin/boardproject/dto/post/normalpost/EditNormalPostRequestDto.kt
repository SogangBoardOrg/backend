package com.kotlin.boardproject.dto.post.normalpost

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class EditNormalPostRequestDto(
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
