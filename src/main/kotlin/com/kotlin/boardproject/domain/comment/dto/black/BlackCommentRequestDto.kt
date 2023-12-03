package com.kotlin.boardproject.domain.comment.dto.black

import com.kotlin.boardproject.global.enums.BlackReason
import javax.validation.constraints.NotNull

class BlackCommentRequestDto(
    @field: NotNull
    val blackReason: BlackReason,
)
