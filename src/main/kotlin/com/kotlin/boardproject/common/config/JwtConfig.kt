package com.kotlin.boardproject.common.config

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
    fun jwtTokenProvider(): com.kotlin.boardproject.auth.AuthTokenProvider {
        return com.kotlin.boardproject.auth.AuthTokenProvider(secret)
    }
}
