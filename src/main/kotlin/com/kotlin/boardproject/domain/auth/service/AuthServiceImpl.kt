package com.kotlin.boardproject.domain.auth.service

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.kotlin.boardproject.global.util.AUTHORITIES_KEY
import com.kotlin.boardproject.global.util.AuthTokenProvider
import com.kotlin.boardproject.global.enums.ProviderType
import com.kotlin.boardproject.global.config.properties.AppProperties
import com.kotlin.boardproject.global.enums.ErrorCode
import com.kotlin.boardproject.global.enums.Role
import com.kotlin.boardproject.global.exception.ConditionConflictException
import com.kotlin.boardproject.global.exception.UnAuthorizedException
import com.kotlin.boardproject.global.util.getAccessToken
import com.kotlin.boardproject.global.util.getCookie
import com.kotlin.boardproject.global.util.log
import com.kotlin.boardproject.domain.auth.dto.TokenDto
import com.kotlin.boardproject.domain.user.dto.UserInfoDto
import com.kotlin.boardproject.domain.user.dto.UserLoginMobileRequestDto
import com.kotlin.boardproject.domain.user.dto.UserLoginRequestDto
import com.kotlin.boardproject.domain.user.dto.UserSignUpDto
import com.kotlin.boardproject.domain.user.dto.UserSignUpMobileDto
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.global.repository.REFRESH_TOKEN
import com.kotlin.boardproject.global.repository.RedisRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.EntityNotFoundException
import javax.servlet.http.HttpServletRequest

private const val THREE_DAYS_MSEC = 259200000

@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val appProperties: AppProperties,
    private val authTokenProvider: AuthTokenProvider,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val redisRepository: RedisRepository,
) : AuthService {
    override fun saveUser(signUpDto: UserSignUpDto): UUID {
        checkEmailAndUserName(signUpDto.email, signUpDto.username)
        val user = signUpDto.toUser()
        val encodedPassword = passwordEncoder.encode(user.password)
        user.encodePassword(encodedPassword)
        return userRepository.save(user).id!!
    }

    override fun loginUser(userLoginRequestDto: UserLoginRequestDto): UserInfoDto {
        val email = userLoginRequestDto.email
        val rawPassword = userLoginRequestDto.password

        val findUser = userRepository.findByEmail(email)
            ?: throw EntityNotFoundException("$email 을 가진 유저는 존재하지 않습니다.")

        if (!passwordEncoder.matches(rawPassword, findUser.password)) {
            throw UnAuthorizedException(ErrorCode.PASSWORD_MISS_MATCH, "비밀번호가 일치하지 않습니다!")
        }

        val (accessToken, refreshToken) = createTokens(findUser, email)
        return UserInfoDto(findUser, accessToken, refreshToken)
    }

    override fun refreshUserToken(request: HttpServletRequest): TokenDto {
        val accessToken = getAccessToken(request)
            ?: throw UnAuthorizedException(ErrorCode.ACCESS_TOKEN_NOT_EXIST, "Access Token이 존재하지 않습니다.")

        val convertAccessToken = authTokenProvider.convertAuthToken(accessToken)

        val claims = convertAccessToken.expiredTokenClaims
            ?: throw UnAuthorizedException(ErrorCode.TOKEN_NOT_EXPIRED, "Access Token이 만료되지 않았거나 올바르지 않습니다.")

        val email = claims.subject
        val role = Role.of(claims.get(AUTHORITIES_KEY, String::class.java))

        val refreshToken = getCookie(request, REFRESH_TOKEN)?.value
            ?: throw UnAuthorizedException(ErrorCode.REFRESH_TOKEN_NOT_EXIST, "Refresh Token이 존재하지 않습니다.")

        val convertRefreshToken = authTokenProvider.convertAuthToken(refreshToken)

        if (!convertRefreshToken.isValid) {
            throw UnAuthorizedException(ErrorCode.TOKEN_INVALID, "올바르지 않은 토큰입니다. ( $refreshToken )")
        }

        val savedRefreshToken = redisRepository.getRefreshTokenByEmail(email)

        if (savedRefreshToken == null || savedRefreshToken != refreshToken) {
            throw UnAuthorizedException(ErrorCode.TOKEN_MISS_MATCH, "Refresh Token이 올바르지 않습니다.")
        }

        val now = Date()
        val tokenExpiry = appProperties.auth.tokenExpiry

        val newAccessToken = authTokenProvider.createAuthToken(
            email,
            Date(now.time + tokenExpiry),
            role.code,
        ).token

        val validTime = convertRefreshToken.tokenClaims!!.expiration.time - now.time

        if (validTime <= THREE_DAYS_MSEC) {
            val refreshTokenExpiry = appProperties.auth.refreshTokenExpiry
            val newRefreshToken = authTokenProvider.createAuthToken(
                email,
                Date(now.time + refreshTokenExpiry),
            ).token

            return TokenDto(newAccessToken, newRefreshToken)
        }

        return TokenDto(newAccessToken, null)
    }

    override fun getUserInfo(email: String): UserInfoDto {
        val findUser = userRepository.findByEmail(email)
            ?: throw EntityNotFoundException("$email 을 가진 유저는 존재하지 않습니다.")

        return UserInfoDto(findUser)
    }

    private fun createTokens(findUser: User, email: String): Pair<String, String> {
        val role = findUser.role

        val now = Date()
        val tokenExpiry = appProperties.auth.tokenExpiry
        val refreshTokenExpiry = appProperties.auth.refreshTokenExpiry

        val accessToken = authTokenProvider.createAuthToken(
            email,
            Date(now.time + tokenExpiry),
            role.code,
        ).token

        val refreshToken = authTokenProvider.createAuthToken(
            email,
            Date(now.time + refreshTokenExpiry),
        ).token

        redisRepository.setRefreshTokenByEmail(email, refreshToken)

        log.info("refreshToken: $refreshToken")
        log.info("accessToken: $accessToken")

        return accessToken to refreshToken
    }

    override fun checkDuplicateEmail(email: String): Boolean {
        return userRepository.findByEmail(email) != null
    }

    override fun checkDuplicateNickname(nickname: String): Boolean {
        return userRepository.findByNickname(nickname) != null
    }

    override fun loginUserMobile(userLoginMobileRequestDto: UserLoginMobileRequestDto): UserInfoDto {
        val token = verifyGoogleToken(
            userLoginMobileRequestDto.tokenString,
            userLoginMobileRequestDto.clientId,
        )

        token.email ?: throw UnAuthorizedException(ErrorCode.TOKEN_INVALID, "올바르지 않은 Google Token 입니다.")
        val email = token.email!!

        val findUser = userRepository.findByEmail(email)
            ?: throw EntityNotFoundException("$email 을 가진 유저는 존재하지 않습니다.")

        val (accessToken, refreshToken) = createTokens(findUser, email)
        return UserInfoDto(findUser, accessToken, refreshToken)
    }

    override fun saveUserGoogleMobile(userSignUpMobileDto: UserSignUpMobileDto): UUID {
        val token = verifyGoogleToken(
            userSignUpMobileDto.tokenString,
            userSignUpMobileDto.clientId,
        )

        token.email ?: throw UnAuthorizedException(ErrorCode.TOKEN_INVALID, "올바르지 않은 Google Token 입니다.")
        checkEmailAndUserName(token.email!!, userSignUpMobileDto.nickname)
        val user = convertGoogleTokenToUser(token, userSignUpMobileDto.nickname)

        return userRepository.save(user).id!!
    }

    private fun convertGoogleTokenToUser(token: GoogleIdToken.Payload, nickname: String): User {
        return User(
            email = token.email,
            nickname = nickname,
            providerType = ProviderType.GOOGLE,
            profileImageUrl = token.get("picture") as String?,
            providerId = token.subject,
        )
    }

    private fun verifyGoogleToken(token: String, clientId: String): GoogleIdToken.Payload {
        return GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
            .setAudience(listOf(clientId))
            .build()
            .verify(token)
            .payload
            ?: throw UnAuthorizedException(ErrorCode.TOKEN_INVALID, "올바르지 않은 Google Token 입니다.")
    }

    private fun checkEmailAndUserName(email: String, username: String) {
        userRepository.findByEmailOrNickname(email, username)?.let {
            if (it.email == email) {
                throw ConditionConflictException(ErrorCode.EMAIL_DUPLICATED, "$email 은 중복 이메일입니다.")
            }
            throw ConditionConflictException(
                ErrorCode.USERNAME_DUPLICATED,
                "$username 은 중복 닉네임입니다.",
            )
        }
    }
}
