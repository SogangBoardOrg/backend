package com.kotlin.boardproject.dto

import javax.validation.constraints.NotBlank

data class UserLoginMobileRequestDto(
    @field:NotBlank
    val tokenString: String,
    @field:NotBlank
    val clientId: String,
)
