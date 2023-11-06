package com.kotlin.boardproject.domain.auth

import com.kotlin.boardproject.domain.auth.dto.TokenResponseDto
import com.kotlin.boardproject.domain.auth.service.AuthService
import com.kotlin.boardproject.domain.user.dto.UserInfoResponseDto
import com.kotlin.boardproject.domain.user.dto.UserLoginMobileRequestDto
import com.kotlin.boardproject.domain.user.dto.UserLoginRequestDto
import com.kotlin.boardproject.domain.user.dto.UserSignUpDto
import com.kotlin.boardproject.domain.user.dto.UserSignUpMobileDto
import com.kotlin.boardproject.global.config.properties.AppProperties
import com.kotlin.boardproject.global.dto.ApiResponse
import com.kotlin.boardproject.global.repository.REFRESH_TOKEN
import com.kotlin.boardproject.global.util.addCookie
import com.kotlin.boardproject.global.util.deleteCookie
import java.util.UUID
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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

    @PostMapping("/signup-mobile")
    fun signUp(@RequestBody userSignUpMobileDto: UserSignUpMobileDto): UUID {
        return authService.saveUserGoogleMobile(userSignUpMobileDto)
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

    @PostMapping("/login-mobile")
    fun login(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @RequestBody userLoginMobileRequestDto: UserLoginMobileRequestDto,
    ): ApiResponse<UserInfoResponseDto> {
        val userInfoDto = authService.loginUserMobile(userLoginMobileRequestDto)

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
