package com.kotlin.boardproject.service

import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.global.enums.ProviderType
import com.kotlin.boardproject.global.enums.Role
import java.util.UUID

fun makeUser(): List<User> {
    val userOne = User(
        id = UUID.randomUUID(),
        email = "userOne@test.com",
        nickname = "userOne",
        role = Role.ROLE_VERIFIED_USER,
        providerType = ProviderType.LOCAL,
    )

    val userTwo = User(
        id = UUID.randomUUID(),
        email = "userTwo@test.com",
        nickname = "userTwo",
        role = Role.ROLE_VERIFIED_USER,
        providerType = ProviderType.LOCAL,
    )

    val userThree = User(
        id = UUID.randomUUID(),
        email = "userThree@test.com",
        nickname = "userThree",
        role = Role.ROLE_VERIFIED_USER,
        providerType = ProviderType.LOCAL,
    )

    return listOf(userOne, userTwo, userThree)
}
