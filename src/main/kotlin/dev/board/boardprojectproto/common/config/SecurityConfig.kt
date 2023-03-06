package dev.board.boardprojectproto.common.config

import dev.board.boardprojectproto.common.enums.Role
import dev.board.boardprojectproto.common.exception.RestAuthenticationEntryPoint
import dev.board.boardprojectproto.common.handler.OAuth2AuthenticationFailureHandler
// import dev.board.boardprojectproto.common.handler.OAuth2AuthenticationSuccessHandler
import dev.board.boardprojectproto.common.handler.TokenAccessDeniedHandler
import dev.board.boardprojectproto.repository.common.OAuth2AuthorizationRequestBasedOnCookieRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsUtils

@Configuration
@EnableMethodSecurity
@EnableWebSecurity(debug = true)
class SpringSecurityConfig(
    private val tokenAccessDeniedHandler: TokenAccessDeniedHandler,
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

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
            // .userService(oAuth2UserService)
            .and()
            // .successHandler(oAuth2AuthenticationSuccessHandler())
            // .failureHandler(oAuth2AuthenticationFailureHandler())

        // http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun oAuth2AuthorizationRequestBasedOnCookieRepository(): OAuth2AuthorizationRequestBasedOnCookieRepository {
        return OAuth2AuthorizationRequestBasedOnCookieRepository()
    }

    // Oauth 인증 실패 핸들러
//    @Bean
//    fun oAuth2AuthenticationSuccessHandler(): OAuth2AuthenticationSuccessHandler {
//        return OAuth2AuthenticationSuccessHandler(
//            appProperties,
//            oAuth2AuthorizationRequestBasedOnCookieRepository(),
//            authTokenProvider,
//            redisRepository,
//            userRepository,
//        )
//    }

//    @Bean
//    fun oAuth2AuthenticationFailureHandler(): OAuth2AuthenticationFailureHandler {
//        return OAuth2AuthenticationFailureHandler(
//            oAuth2AuthorizationRequestBasedOnCookieRepository(),
//        )
//    }

}
