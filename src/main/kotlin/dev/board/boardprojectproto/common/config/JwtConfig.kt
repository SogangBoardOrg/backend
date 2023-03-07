package dev.board.boardprojectproto.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JwtConfig(
    // TODO: 값 변경하기
    @Value("\${jwt.secret}")
    private val secret: String,
) {
    @Bean
    fun jwtTokenProvider(): dev.board.boardprojectproto.auth.AuthTokenProvider {
        return dev.board.boardprojectproto.auth.AuthTokenProvider(secret)
    }
}
