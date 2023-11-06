package com.kotlin.boardproject.domain.auth.service

import com.kotlin.boardproject.domain.auth.dto.TokenDto
import com.kotlin.boardproject.domain.user.dto.UserInfoDto
import com.kotlin.boardproject.domain.user.dto.UserLoginMobileRequestDto
import com.kotlin.boardproject.domain.user.dto.UserLoginRequestDto
import com.kotlin.boardproject.domain.user.dto.UserSignUpDto
import com.kotlin.boardproject.domain.user.dto.UserSignUpMobileDto
import java.util.*
import javax.servlet.http.HttpServletRequest

interface AuthService {
    fun saveUser(userSingUpDto: UserSignUpDto): UUID

    fun loginUser(userLoginRequestDto: UserLoginRequestDto): UserInfoDto

    fun refreshUserToken(request: HttpServletRequest): TokenDto

    fun getUserInfo(email: String): UserInfoDto

    fun checkDuplicateEmail(email: String): Boolean

    fun checkDuplicateNickname(nickname: String): Boolean

    fun saveUserGoogleMobile(userSignUpMobileDto: UserSignUpMobileDto): UUID

    fun loginUserMobile(userLoginMobileRequestDto: UserLoginMobileRequestDto): UserInfoDto
}
