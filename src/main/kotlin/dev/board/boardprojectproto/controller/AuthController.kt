package dev.board.boardprojectproto.controller

import dev.board.boardprojectproto.common.config.properties.AppProperties
import dev.board.boardprojectproto.common.util.addCookie
import dev.board.boardprojectproto.common.util.deleteCookie
import dev.board.boardprojectproto.dto.UserInfoResponseDto
import dev.board.boardprojectproto.dto.UserLoginRequestDto
import dev.board.boardprojectproto.dto.UserSignUpDto
import dev.board.boardprojectproto.dto.common.ApiResponse
import dev.board.boardprojectproto.repository.common.REFRESH_TOKEN
import dev.board.boardprojectproto.service.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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
}
