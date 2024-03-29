package com.kotlin.boardproject.domain.user.dto

import javax.validation.constraints.NotBlank

data class UserSignUpMobileDto(
    @field:NotBlank
    val tokenString: String,
    @field:NotBlank
    val nickname: String,
    @field:NotBlank
    val clientId: String,
)
