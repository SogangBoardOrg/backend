package com.kotlin.boardproject.global.util

import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.global.enums.ProviderType
import com.kotlin.boardproject.global.enums.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.core.user.OAuth2User
import java.util.Collections

class UserPrincipal(
    private val userId: String,
    private val password: String,
    private val providerType: ProviderType,
    private val roleType: Role,
    private val authorities: MutableCollection<GrantedAuthority>,
) : OAuth2User, UserDetails, OidcUser {

    private lateinit var attributes: MutableMap<String, Any>

    override fun getName(): String {
        return userId
    }

    override fun getAttributes(): MutableMap<String, Any> {
        return attributes
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return userId
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getClaims(): MutableMap<String, Any>? {
        return null
    }

    override fun getUserInfo(): OidcUserInfo? {
        return null
    }

    override fun getIdToken(): OidcIdToken? {
        return null
    }

    companion object {
        fun create(user: User): UserPrincipal {
            return UserPrincipal(
                user.id.toString(),
                user.password,
                user.providerType,
                Role.ROLE_NEWBIE,
                Collections.singletonList(SimpleGrantedAuthority(Role.ROLE_NEWBIE.name)),
            )
        }

        fun create(user: User, attributes: MutableMap<String, Any>): UserPrincipal {
            val userPrincipal = create(user)
            userPrincipal.attributes = attributes

            return userPrincipal
        }
    }
}
