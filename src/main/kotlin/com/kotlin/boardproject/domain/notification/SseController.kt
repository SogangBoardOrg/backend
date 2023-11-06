package com.kotlin.boardproject.domain.notification

import com.kotlin.boardproject.global.annotation.LoginUser
import com.kotlin.boardproject.global.dto.ApiResponse
import com.kotlin.boardproject.domain.notification.service.SseService
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/v1")
class SseController(
    private val sseService: SseService,
) {

    @GetMapping("/subscribe", produces = ["text/event-stream"])
    fun subscribe(
        @LoginUser loginUser: User,
        @RequestHeader("Last-Event-ID", required = false, defaultValue = "") lastEventId: String,
    ): SseEmitter {
        return sseService.subscribe(loginUser.username, lastEventId)
    }

    @PostMapping("/test")
    fun test(): ApiResponse<String> {
        sseService.testSend()
        return ApiResponse.success("success")
    }
}
