package com.kotlin.boardproject.domain.post.dto.black

import com.kotlin.boardproject.global.enums.BlackReason
import javax.validation.constraints.NotNull

data class BlackPostRequestDto(
    @field: NotNull
    val blackReason: BlackReason,
)
