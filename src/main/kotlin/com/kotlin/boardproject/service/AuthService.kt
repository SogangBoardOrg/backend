package com.kotlin.boardproject.service

import com.kotlin.boardproject.dto.TokenDto
import com.kotlin.boardproject.dto.UserInfoDto
import com.kotlin.boardproject.dto.UserLoginMobileRequestDto
import com.kotlin.boardproject.dto.UserLoginRequestDto
import com.kotlin.boardproject.dto.UserSignUpDto
import com.kotlin.boardproject.dto.UserSignUpMobileDto
import java.util.UUID
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
