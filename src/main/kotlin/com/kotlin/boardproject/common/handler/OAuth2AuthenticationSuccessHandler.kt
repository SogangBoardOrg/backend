package com.kotlin.boardproject.common.handler

import com.kotlin.boardproject.auth.OAuth2UserInfoFactory
import com.kotlin.boardproject.auth.ProviderType
import com.kotlin.boardproject.common.config.properties.AppProperties
import com.kotlin.boardproject.common.enums.ErrorCode
import com.kotlin.boardproject.common.exception.UnAuthorizedException
import com.kotlin.boardproject.common.util.addCookie
import com.kotlin.boardproject.common.util.deleteCookie
import com.kotlin.boardproject.common.util.log
import com.kotlin.boardproject.model.User
import com.kotlin.boardproject.repository.common.RedisRepository
import com.kotlin.boardproject.repository.UserRepository
import com.kotlin.boardproject.repository.common.OAuth2AuthorizationRequestBasedOnCookieRepository
import com.kotlin.boardproject.repository.common.REDIRECT_URI_PARAM_COOKIE_NAME
import com.kotlin.boardproject.repository.common.REFRESH_TOKEN
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.WebUtils.getCookie
import java.net.URI
import java.util.*
import javax.persistence.EntityNotFoundException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class OAuth2AuthenticationSuccessHandler(
    private val appProperties: AppProperties,
    private val authorizationRequestRepository: OAuth2AuthorizationRequestBasedOnCookieRepository,
    private val tokenProvider: com.kotlin.boardproject.auth.AuthTokenProvider,
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

        return userRepository.findByEmailOrProviderId(userInfo.getEmail(), userInfo.getId())
            ?: throw EntityNotFoundException(
                "유저를 찾을 수 없습니다. email = [${userInfo.getEmail()}], providerId = [${userInfo.getId()}] )",
            )
    }

    private fun setRefreshTokenCookie(
        request: HttpServletRequest,
        response: HttpServletResponse,
        refreshToken: com.kotlin.boardproject.auth.AuthToken,
    ) {
        val cookieMaxAge = appProperties.auth.refreshTokenExpiry / 1000
        deleteCookie(request, response, REFRESH_TOKEN)
        addCookie(response, REFRESH_TOKEN, refreshToken.token, cookieMaxAge)
    }

    private fun createTokens(findUser: User): Pair<com.kotlin.boardproject.auth.AuthToken, com.kotlin.boardproject.auth.AuthToken> {
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
        // TODO: redis 추가하기
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
