package com.kotlin.boardproject.controller

import com.kotlin.boardproject.auth.LoginUser
import com.kotlin.boardproject.dto.common.ApiResponse
import com.kotlin.boardproject.service.SseService
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*
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