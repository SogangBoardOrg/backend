package com.kotlin.boardproject.controller

import com.kotlin.boardproject.common.config.properties.AppProperties
import com.kotlin.boardproject.common.util.addCookie
import com.kotlin.boardproject.common.util.deleteCookie
import com.kotlin.boardproject.dto.TokenResponseDto
import com.kotlin.boardproject.dto.UserInfoResponseDto
import com.kotlin.boardproject.dto.UserLoginRequestDto
import com.kotlin.boardproject.dto.UserSignUpDto
import com.kotlin.boardproject.dto.common.ApiResponse
import com.kotlin.boardproject.repository.common.REFRESH_TOKEN
import com.kotlin.boardproject.service.AuthService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.constraints.NotBlank

@Validated
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val appProperties: AppProperties,
) {

    @PostMapping("/signup")
    fun signUp(@RequestBody userSignUpDto: UserSignUpDto): UUID {
        return authService.saveUser(userSignUpDto)
    }

    @PostMapping("/login")
    fun login(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @RequestBody userLoginRequestDto: UserLoginRequestDto,
    ): ApiResponse<UserInfoResponseDto> {
        val userInfoDto = authService.loginUser(userLoginRequestDto)

        val cookieMaxAge = appProperties.auth.refreshTokenExpiry / 1000

        deleteCookie(request, response, REFRESH_TOKEN)
        addCookie(response, REFRESH_TOKEN, userInfoDto.refreshToken!!, cookieMaxAge)

        return ApiResponse.success(UserInfoResponseDto(userInfoDto))
    }

    @GetMapping("/duplicate-email")
    fun checkDuplicateEmail(
        @RequestParam @NotBlank
        email: String,
    ): ApiResponse<Boolean> {
        return ApiResponse.success(authService.checkDuplicateEmail(email))
    }

    @GetMapping("/duplicate-nickname")
    fun checkDuplicateNickname(
        @RequestParam @NotBlank
        nickname: String,
    ): ApiResponse<Boolean> {
        return ApiResponse.success(authService.checkDuplicateNickname(nickname))
    }

    @GetMapping("/refresh")
    fun refresh(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ApiResponse<TokenResponseDto> {
        val (accessToken, refreshToken) = authService.refreshUserToken(request)

        if (refreshToken != null) {
            val cookieMaxAge = appProperties.auth.refreshTokenExpiry / 1000

            deleteCookie(request, response, REFRESH_TOKEN)
            addCookie(response, REFRESH_TOKEN, refreshToken, cookieMaxAge)
        }

        return ApiResponse.success(TokenResponseDto(accessToken))
    }
}
