package com.kotlin.boardproject.service

import com.kotlin.boardproject.model.Notification
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Service
interface SseService {
    fun subscribe(
        userEmail: String,
        lastEventId: String,
    ): SseEmitter

    fun sendEvent(
        userEmail: String,
        notification: Notification?,
    )

    fun testSend()
}
