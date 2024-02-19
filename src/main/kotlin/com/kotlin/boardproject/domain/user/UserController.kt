package com.kotlin.boardproject.domain.user

import com.kotlin.boardproject.domain.user.dto.NicknameChangeDto
import com.kotlin.boardproject.domain.user.dto.ProfileImageUrlChangeDto
import com.kotlin.boardproject.domain.user.service.UserService
import com.kotlin.boardproject.global.annotation.LoginUser
import com.kotlin.boardproject.global.dto.ApiResponse
import org.springframework.security.core.userdetails.User
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService,
) {

    @PutMapping("/profile-image")
    fun changeProfileImage(
        @LoginUser loginUser: User,
        @RequestBody profileImageUrlChangeDto: ProfileImageUrlChangeDto,
    ): ApiResponse<String> {
        userService.changeProfileImage(loginUser.username, profileImageUrlChangeDto.profileImageUrl)

        return ApiResponse.success("프로필 이미지 변경 성공")
    }

    @PutMapping("/nickname")
    fun changeNickname(
        @LoginUser loginUser: User,
        @RequestBody nicknameChangeDto: NicknameChangeDto,
    ): ApiResponse<String> {
        userService.changeNickname(loginUser.username, nicknameChangeDto.nickname)

        return ApiResponse.success("닉네임 이미지 변경 성공")
    }
}
