package com.kotlin.boardproject.service

import com.kotlin.boardproject.auth.OAuth2UserInfo
import com.kotlin.boardproject.auth.OAuth2UserInfoFactory
import com.kotlin.boardproject.auth.ProviderType
import com.kotlin.boardproject.auth.UserPrincipal
import com.kotlin.boardproject.common.enums.ErrorCode
import com.kotlin.boardproject.common.exception.InternalServiceException
import com.kotlin.boardproject.common.exception.OAuthProviderMissMatchException
import com.kotlin.boardproject.model.User
import com.kotlin.boardproject.repository.UserRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository,
) : DefaultOAuth2UserService() {
    // 받아온 token을 분석해서 필요한 정보를 넘겨주는 역할
    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User {
        val user = super.loadUser(userRequest)

        return runCatching {
            process(userRequest!!, user)
        }.onFailure {
            if (it is OAuthProviderMissMatchException) {
                throw it
            }
            throw InternalServiceException(ErrorCode.SERVER_ERROR, it.message.toString())
        }.getOrThrow()
    }

    private fun process(userRequest: OAuth2UserRequest, user: OAuth2User): OAuth2User {
        val providerType =
            ProviderType.valueOf(userRequest.clientRegistration.registrationId.uppercase(Locale.getDefault()))

        val accessToken = userRequest.accessToken.tokenValue

        val attributes = user.attributes.toMutableMap()
        // 유저가 존재하거나 존재하지 않으면 UserPrincipal을 생성한다.
        return UserPrincipal.create(getOrCreateUser(providerType, attributes), attributes)
    }

    private fun getOrCreateUser(
        providerType: ProviderType,
        attributes: MutableMap<String, Any>,
    ): User {
        val userInfo = OAuth2UserInfoFactory.getOauth2UserInfo(providerType, attributes)

        val savedUser = userRepository.findByEmail(userInfo.getEmail())
            ?: userRepository.findUserByProviderId(userInfo.getId())

        return savedUser ?: createUser(userInfo, providerType)
    }

    private fun createUser(userInfo: OAuth2UserInfo, providerType: ProviderType): User {
        // TODO: nickname 임의의 닉네임 생성, 임의의 문자열 생성후에 서버에서 중복검사를 해야함, 만약에 중복이면 다시 생성

        // generate random nickname
        var nickname = UUID.randomUUID().toString().substring(0, 32)

        while (userRepository.findUserByNickname(nickname) != null) {
            nickname = UUID.randomUUID().toString().substring(0, 32)
        }

        val user = User(
            email = userInfo.getEmail(),
            nickname = nickname,
            providerType = providerType,
            profileImageUrl = userInfo.getImageUrl(),
            providerId = userInfo.getId(),
        )
        println("user : $user")
        println("user info: " + userInfo.getId())
        println("user name: $nickname")
        println("provider type: $providerType")
        println("user email: " + userInfo.getEmail())

        return userRepository.saveAndFlush(user)
    }
}
