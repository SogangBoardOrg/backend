package com.kotlin.boardproject.controller

import com.kotlin.boardproject.auth.LoginUser
import com.kotlin.boardproject.dto.common.ApiResponse
import com.kotlin.boardproject.dto.notification.GetNotificationsResponseDto
import com.kotlin.boardproject.service.NotificationService
import org.springframework.security.core.userdetails.User
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.Positive

@Validated
@RestController
@RequestMapping("/api/v1/notifications")
class NotificationController(
    private val notificationService: NotificationService,
) {

    @GetMapping("")
    fun notifications(
        @LoginUser loginUser: User,
    ): ApiResponse<GetNotificationsResponseDto> {
        val getNotificationsResponseDto = notificationService.getNotifications(loginUser.username)
        return ApiResponse.success(getNotificationsResponseDto)
    }

    @DeleteMapping("/{notificationId}")
    fun readNotification(
        @PathVariable @Positive
        notificationId: Long,
        @LoginUser loginUser: User,
    ): ApiResponse<String> {
        notificationService.readNotificationByEmailAndNotificationId(loginUser.username, notificationId)
        return ApiResponse.success("success")
    }

    @DeleteMapping("")
    fun readAllNotifications(
        @LoginUser loginUser: User,
    ): ApiResponse<String> {
        notificationService.readAllUnreadNotificationByEmail(loginUser.username)
        return ApiResponse.success("success")
    }
}
