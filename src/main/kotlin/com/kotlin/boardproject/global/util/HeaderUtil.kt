package com.kotlin.boardproject.global.util

import javax.servlet.http.HttpServletRequest

const val HEADER_AUTHORIZATION = "Authorization"
const val TOKEN_PREFIX = "Bearer "

fun getAccessToken(request: HttpServletRequest): String? {
    val headerValue = request.getHeader(HEADER_AUTHORIZATION)
    return if (headerValue?.startsWith(TOKEN_PREFIX) == true) headerValue.substring(TOKEN_PREFIX.length) else null
}
