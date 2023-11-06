package com.kotlin.boardproject.global.handler

import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.global.config.properties.AppProperties
import com.kotlin.boardproject.global.enums.ErrorCode
import com.kotlin.boardproject.global.enums.ProviderType
import com.kotlin.boardproject.global.exception.UnAuthorizedException
import com.kotlin.boardproject.global.repository.OAuth2AuthorizationRequestBasedOnCookieRepository
import com.kotlin.boardproject.global.repository.REDIRECT_URI_PARAM_COOKIE_NAME
import com.kotlin.boardproject.global.repository.REFRESH_TOKEN
import com.kotlin.boardproject.global.repository.RedisRepository
import com.kotlin.boardproject.global.util.AuthToken
import com.kotlin.boardproject.global.util.AuthTokenProvider
import com.kotlin.boardproject.global.util.OAuth2UserInfoFactory
import com.kotlin.boardproject.global.util.addCookie
import com.kotlin.boardproject.global.util.deleteCookie
import com.kotlin.boardproject.global.util.log
import java.net.URI
import java.util.*
import javax.persistence.EntityNotFoundException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.WebUtils.getCookie

@Component
class OAuth2AuthenticationSuccessHandler(
    private val appProperties: AppProperties,
    private val authorizationRequestRepository: OAuth2AuthorizationRequestBasedOnCookieRepository,
    private val tokenProvider: AuthTokenProvider,
    private val redisRepository: RedisRepository,
    private val userRepository: UserRepository,
) : SimpleUrlAuthenticationSuccessHandler() {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val targetUrl = determineTargetUrl(request, response, authentication)

        if (response.isCommitted) {
            logger.debug("Response has already been committed. Unable to redirect to $targetUrl")
            return
        }

        clearAuthenticationAttributes(request, response)
        redirectStrategy.sendRedirect(request, response, targetUrl)
    }

    override fun determineTargetUrl(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ): String {
        val targetUrl = getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)?.value ?: defaultTargetUrl
        validateRedirectTargetUrl(targetUrl)

        val findUser = findUserByAuthToken(authentication)
        val (accessToken, refreshToken) = createTokens(findUser)
        setRefreshTokenCookie(request, response, refreshToken)
        log.info(refreshToken.toString())
        return UriComponentsBuilder.fromUriString(targetUrl)
            .queryParam("token", accessToken.token)
            .build().toUriString()
    }

    private fun validateRedirectTargetUrl(targetUrl: String) {
        targetUrl.let {
            if (!isAuthorizedRedirectUri(it)) {
                throw UnAuthorizedException(
                    ErrorCode.UNAUTHORIZED,
                    "올바르지 않은 redirect uri ( $it ) 입니다.",
                )
            }
        }
    }

    private fun findUserByAuthToken(authentication: Authentication): User {
        val authToken = authentication as OAuth2AuthenticationToken
        val providerType = ProviderType.valueOf(authToken.authorizedClientRegistrationId.uppercase(Locale.getDefault()))

        val user = authentication.principal as OidcUser
        val userInfo = OAuth2UserInfoFactory.getOauth2UserInfo(providerType, user.attributes)

        return userRepository.findByEmailOrProviderId(userInfo.email, userInfo.id)
            ?: throw EntityNotFoundException(
                "유저를 찾을 수 없습니다. email = [${userInfo.email}], providerId = [${userInfo.id}] )",
            )
    }

    private fun setRefreshTokenCookie(
        request: HttpServletRequest,
        response: HttpServletResponse,
        refreshToken: AuthToken,
    ) {
        val cookieMaxAge = appProperties.auth.refreshTokenExpiry / 1000
        deleteCookie(request, response, REFRESH_TOKEN)
        addCookie(response, REFRESH_TOKEN, refreshToken.token, cookieMaxAge)
    }

    private fun createTokens(findUser: User): Pair<AuthToken, AuthToken> {
        val now = Date()
        val tokenExpiry = appProperties.auth.tokenExpiry
        val refreshTokenExpiry = appProperties.auth.refreshTokenExpiry

        val accessToken = tokenProvider.createAuthToken(
            findUser.email,
            Date(now.time + tokenExpiry),
            findUser.role.code,
        )

        val refreshToken = tokenProvider.createAuthToken(
            findUser.email,
            Date(now.time + refreshTokenExpiry),
        )
        redisRepository.setRefreshTokenByEmail(findUser.email, refreshToken.token)

        return accessToken to refreshToken
    }

    fun clearAuthenticationAttributes(request: HttpServletRequest, response: HttpServletResponse) {
        super.clearAuthenticationAttributes(request)
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response)
    }

    private fun isAuthorizedRedirectUri(uri: String): Boolean {
        val clientRedirectUri = URI.create(uri)
        return appProperties.oAuth2.authorizedRedirectUris
            .any {
                val authorizedURI = URI.create(it)
                authorizedURI.host.equals(clientRedirectUri.host, ignoreCase = true) &&
                    authorizedURI.port == clientRedirectUri.port
            }
    }
}
