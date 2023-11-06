package com.kotlin.boardproject.domain.notification

import com.kotlin.boardproject.domain.notification.dto.GetNotificationsResponseDto
import com.kotlin.boardproject.domain.notification.service.NotificationService
import com.kotlin.boardproject.global.annotation.LoginUser
import com.kotlin.boardproject.global.dto.ApiResponse
import org.springframework.security.core.userdetails.User
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
