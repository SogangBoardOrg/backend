package dev.board.boardprojectproto.service

import dev.board.boardprojectproto.common.config.properties.AppProperties
import dev.board.boardprojectproto.common.enums.ErrorCode
import dev.board.boardprojectproto.common.exception.UnAuthorizedException
import dev.board.boardprojectproto.dto.UserInfoDto
import dev.board.boardprojectproto.dto.UserLoginRequestDto
import dev.board.boardprojectproto.dto.UserSignUpDto
import dev.board.boardprojectproto.model.User
import dev.board.boardprojectproto.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val appProperties: AppProperties,
    private val authTokenProvider: dev.board.boardprojectproto.auth.AuthTokenProvider,
    private val passwordEncoder: BCryptPasswordEncoder,
) : AuthService {
    override fun saveUser(signUpDto: UserSignUpDto): UUID {
        // TODO: 이메일 중복 확인
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

    private fun createTokens(
        findUser: User,
        email: String,
    ): Pair<String, String> {
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

        // redisRepository.setRefreshTokenByEmail(email, refreshToken)

        return accessToken to refreshToken
    }
}
