package com.kotlin.boardproject.dto.post

import com.kotlin.boardproject.common.enums.BlackReason
import javax.validation.constraints.NotNull

data class BlackPostRequestDto(
    @field: NotNull
    val blackReason: BlackReason,
)
