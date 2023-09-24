package com.kotlin.boardproject.dto.comment

import com.kotlin.boardproject.common.enums.BlackReason
import javax.validation.constraints.NotNull

class BlackCommentRequestDto(
    @field: NotNull
    val blackReason: BlackReason,
)
