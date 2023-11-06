package com.kotlin.boardproject.domain.notification.service

import com.kotlin.boardproject.domain.notification.domain.Notification
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
