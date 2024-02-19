package com.kotlin.boardproject.domain.user.service

interface UserService {
    fun changeProfileImage(userEmail: String, profileImageUrl: String)

    fun changeNickname(userEmail: String, nickname: String)
}
