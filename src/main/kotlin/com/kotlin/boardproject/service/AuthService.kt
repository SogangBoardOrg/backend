package com.kotlin.boardproject.service

import com.kotlin.boardproject.dto.TokenDto
import com.kotlin.boardproject.dto.UserInfoDto
import com.kotlin.boardproject.dto.UserLoginRequestDto
import com.kotlin.boardproject.dto.UserSignUpDto
import java.util.*
import javax.servlet.http.HttpServletRequest

interface AuthService {
    fun saveUser(userSingUpDto: UserSignUpDto): UUID

    fun loginUser(userLoginRequestDto: UserLoginRequestDto): UserInfoDto

    fun refreshUserToken(request: HttpServletRequest): TokenDto

    fun getUserInfo(email: String): UserInfoDto

    fun checkDuplicateEmail(email: String): Boolean

    fun checkDuplicateNickname(nickname: String): Boolean
}
