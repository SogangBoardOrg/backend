package com.kotlin.boardproject.domain.post.dto.create

import com.kotlin.boardproject.global.enums.PostType
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

data class CreatePostRequestDto(
    @field: NotBlank
    val title: String,
    @field: NotBlank
    val content: String,
    @field: NotNull
    val isAnon: Boolean,
    @field: NotNull
    val commentOn: Boolean,
    @field: NotNull
    val postType: PostType,
    @field: Positive
    val reviewScore: Int? = null,
    @field: Positive
    val courseId: Long? = null,
    @field: Valid
    val photoList: List<@NotBlank String>,
)
