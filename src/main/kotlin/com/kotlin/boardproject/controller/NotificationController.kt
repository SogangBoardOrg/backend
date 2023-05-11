package com.kotlin.boardproject.controller

import com.kotlin.boardproject.auth.LoginUser
import com.kotlin.boardproject.dto.common.ApiResponse
import com.kotlin.boardproject.dto.notification.GetNotificationsResponseDto
import com.kotlin.boardproject.service.NotificationService
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/notifications")
class NotificationController(
    private val notificationService: NotificationService,
) {

//    @GetMapping(value = ["/subscribe"], produces = ["text/event-stream"])
//    fun subscribe(
//        @LoginUser loginUser: User,
//    ): SseEmitter {
//        log.info("test")
//        log.info(loginUser.toString())
//        log.info("test")
//        // notificationService.subscribe(loginUser.username)
//        // return notificationService.subscribe(id)
//        return SseEmitter()
//    }

    @GetMapping("")
    fun notifications(
        @LoginUser loginUser: User,
    ): ApiResponse<GetNotificationsResponseDto> {
        val getNotificationsResponseDto = notificationService.getNotifications(loginUser.username)
        return ApiResponse.success(getNotificationsResponseDto)
    }

    @DeleteMapping("/{id}")
    fun readNotification(
        @PathVariable id: Long,
        @LoginUser loginUser: User,
    ): ApiResponse<String> {
        notificationService.deleteNotificationByEmailAndNotificationId(loginUser.username, id)
        return ApiResponse.success("success")
    }

    @DeleteMapping("")
    fun readAllNotifications(
        @LoginUser loginUser: User,
    ): ApiResponse<String> {
        notificationService.deleteAllUnreadNotificationByEmail(loginUser.username)
        return ApiResponse.success("success")
    }
}
