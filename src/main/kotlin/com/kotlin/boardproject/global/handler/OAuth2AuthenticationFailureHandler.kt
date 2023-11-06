package com.kotlin.boardproject.global.handler

import com.kotlin.boardproject.global.repository.OAuth2AuthorizationRequestBasedOnCookieRepository
import com.kotlin.boardproject.global.repository.REDIRECT_URI_PARAM_COOKIE_NAME
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.WebUtils.getCookie

@Component
class OAuth2AuthenticationFailureHandler(
    private val authorizationRequestRepository: OAuth2AuthorizationRequestBasedOnCookieRepository,
) : SimpleUrlAuthenticationFailureHandler() {
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException,
    ) {
        var targetUrl = getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)?.value ?: "/"

        exception.printStackTrace()

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
            .queryParam("error", exception.localizedMessage)
            .build().toUriString()

        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response)

        redirectStrategy.sendRedirect(request, response, targetUrl)
    }
}
