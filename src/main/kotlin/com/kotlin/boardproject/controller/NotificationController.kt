package com.kotlin.boardproject.controller

import com.kotlin.boardproject.auth.LoginUser
import com.kotlin.boardproject.common.util.log
import com.kotlin.boardproject.dto.common.ApiResponse
import com.kotlin.boardproject.service.NotificationService
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/v1")
class NotificationController(
    private val notificationService: NotificationService,
) {

    @GetMapping(value = ["/subscribe"], produces = ["text/event-stream"])
    fun subscribe(
        @LoginUser loginUser: User,
    ): SseEmitter {
        log.info("test")
        log.info(loginUser.toString())
        log.info("test")
        // notificationService.subscribe(loginUser.username)
        // return notificationService.subscribe(id)
        return SseEmitter()
    }

    @GetMapping("/notifications")
    fun notifications(
        @LoginUser loginUser: User,
    ): ApiResponse<Int> {
        // return ResponseEntity.ok().body(notificationService.findAllByMemberIdAndUnread(loginMemberRequest.getId()))
        return ApiResponse.success(0)
    }

    @PutMapping("/notifications/{id}")
    fun readNotification(
        @PathVariable id: Long,
    ): ApiResponse<Int> {
        // notificationService.readNotification(id)
        return ApiResponse.success(0)
    }

    @PutMapping("/notifications/all")
    fun readAllNotifications(
        @LoginUser loginUser: User,
    ): ApiResponse<Int> {
        // notificationService.readAllNotifications(loginMemberRequest.getId())
        return ApiResponse.success(0)
    }
}
