package com.kotlin.boardproject.domain.user.dto

import javax.validation.constraints.NotBlank

data class UserLoginMobileRequestDto(
    @field:NotBlank
    val tokenString: String,
    @field:NotBlank
    val clientId: String,
)
