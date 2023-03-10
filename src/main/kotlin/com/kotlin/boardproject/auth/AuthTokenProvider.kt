package com.kotlin.boardproject.auth

import com.kotlin.boardproject.common.enums.ErrorCode
import com.kotlin.boardproject.common.exception.UnAuthorizedException
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import java.security.Key
import java.util.*

class AuthTokenProvider(
    secret: String,
) {
    private val key: Key = Keys.hmacShaKeyFor(secret.toByteArray())

    fun createAuthToken(email: String, expiry: Date, role: String? = null): com.kotlin.boardproject.auth.AuthToken {
        return com.kotlin.boardproject.auth.AuthToken(email, expiry, key, role)
    }

    fun convertAuthToken(token: String): com.kotlin.boardproject.auth.AuthToken {
        return com.kotlin.boardproject.auth.AuthToken(token, key)
    }

    fun getAuthentication(authToken: com.kotlin.boardproject.auth.AuthToken): Authentication {
        if (authToken.isValid) {
            val claims = authToken.tokenClaims
            val authorities = arrayOf(claims!![com.kotlin.boardproject.auth.AUTHORITIES_KEY].toString())
                .map(::SimpleGrantedAuthority)
                .toList()

            val principal = User(claims.subject, "", authorities)

            return UsernamePasswordAuthenticationToken(principal, authToken, authorities)
        } else {
            throw UnAuthorizedException(ErrorCode.TOKEN_INVALID, "올바르지 않은 Token입니다.")
        }
    }
}
