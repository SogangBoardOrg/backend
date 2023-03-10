package com.kotlin.boardproject.common.config


import com.kotlin.boardproject.common.config.properties.AppProperties
import com.kotlin.boardproject.common.config.properties.CorsProperties
import com.kotlin.boardproject.common.enums.Role
import com.kotlin.boardproject.common.exception.RestAuthenticationEntryPoint
import com.kotlin.boardproject.common.filter.TokenAuthenticationFilter
import com.kotlin.boardproject.common.handler.OAuth2AuthenticationFailureHandler
import com.kotlin.boardproject.common.handler.OAuth2AuthenticationSuccessHandler
import com.kotlin.boardproject.common.handler.TokenAccessDeniedHandler
import com.kotlin.boardproject.repository.UserRepository
import com.kotlin.boardproject.repository.common.OAuth2AuthorizationRequestBasedOnCookieRepository
import com.kotlin.boardproject.service.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsUtils
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableMethodSecurity
@EnableWebSecurity(debug = true)
class SpringSecurityConfig(
    private val corsProperties: CorsProperties,
    private val appProperties: AppProperties,
    private val authTokenProvider: com.kotlin.boardproject.auth.AuthTokenProvider,
    private val oAuth2UserService: CustomOAuth2UserService,
    // private val redisRepository: RedisRepository,
    private val tokenAccessDeniedHandler: TokenAccessDeniedHandler,
    private val userRepository: UserRepository,
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .exceptionHandling()
            .authenticationEntryPoint(RestAuthenticationEntryPoint()) // 인증 실패
            .accessDeniedHandler(tokenAccessDeniedHandler) // 인가 실패
            .and()
            .authorizeRequests()
            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
            .antMatchers(
                "/",
                "/error",
                "/favicon.ico",
                "/**/*.png",
                "/**/*.gif",
                "/**/*.svg",
                "/**/*.jpg",
                "/**/*.html",
                "/**/*.css",
                "/**/*.js",
            )
            .permitAll()
            .antMatchers("/api/v1/**").permitAll()
            .antMatchers("/api/v2/**").permitAll()
            .antMatchers("/actuator/**").permitAll()
            // 여기에 인증 회원과 뉴비 게시판 나눠야함
            .antMatchers("/api/admin/**").hasAuthority(Role.ROLE_ADMIN.code)
            .anyRequest().authenticated()
            .and()
            .oauth2Login()
            .authorizationEndpoint()
            .baseUri("/oauth2/authorization")
            .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
            .and()
            .redirectionEndpoint()
            .baseUri("/*/oauth2/code/*")
            .and()
            .userInfoEndpoint()
            .userService(oAuth2UserService)
            .and()
            .successHandler(oAuth2AuthenticationSuccessHandler())
            .failureHandler(oAuth2AuthenticationFailureHandler())

        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun oAuth2AuthorizationRequestBasedOnCookieRepository(): OAuth2AuthorizationRequestBasedOnCookieRepository {
        return OAuth2AuthorizationRequestBasedOnCookieRepository()
    }

    @Bean
    fun tokenAuthenticationFilter(): TokenAuthenticationFilter {
        return TokenAuthenticationFilter(authTokenProvider)
    }

    // Oauth 인증 실패 핸들러
    @Bean
    fun oAuth2AuthenticationSuccessHandler(): OAuth2AuthenticationSuccessHandler {
        return OAuth2AuthenticationSuccessHandler(
            appProperties,
            oAuth2AuthorizationRequestBasedOnCookieRepository(),
            authTokenProvider,
            userRepository,
        )
    }

    @Bean
    fun oAuth2AuthenticationFailureHandler(): OAuth2AuthenticationFailureHandler {
        return OAuth2AuthenticationFailureHandler(
            oAuth2AuthorizationRequestBasedOnCookieRepository(),
        )
    }

    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val corsConfigSource = UrlBasedCorsConfigurationSource()
        val corsConfig = CorsConfiguration()
        corsConfig.allowedHeaders = corsProperties.allowedHeaders.split(",")
        corsConfig.allowedMethods = corsProperties.allowedMethods.split(",")
        corsConfig.allowedOrigins = corsProperties.allowedOrigins.split(",")
        corsConfig.allowCredentials = true
        corsConfig.maxAge = corsProperties.maxAge
        corsConfigSource.registerCorsConfiguration("/**", corsConfig)
        return corsConfigSource
    }
}
