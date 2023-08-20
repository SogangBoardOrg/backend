package com.kotlin.boardproject.service

import com.kotlin.boardproject.common.util.log
import com.kotlin.boardproject.dto.notification.NotificationResponseDto
import com.kotlin.boardproject.model.Notification
import com.kotlin.boardproject.repository.NotificationRepository
import com.kotlin.boardproject.repository.UserRepository
import com.kotlin.boardproject.repository.common.SseRepository
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Service
class SseServiceImpl(
    private val sseRepository: SseRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository,
) : SseService {
    override fun subscribe(
        userEmail: String,
        lastEventId: String,
    ): SseEmitter {
        val user = userRepository.findByEmail(userEmail)
            ?: throw IllegalArgumentException("user not found: $userEmail")

        val emitter = SseEmitter(10 * 60 * 1000) // 10분

        sseRepository.put(userEmail, emitter)
        log.info("new emitter added: {}", emitter)
        emitter.onCompletion {
            log.info("onCompletion callback")
            sseRepository.remove(userEmail)
        }
        emitter.onTimeout {
            log.info("onTimeout callback")
            emitter.complete()
        }

        val dummyEvent = SseEmitter
            .event()
            .id("${userEmail}_dummy")
            .name("sse")
            .data("EventStream Created. [userId=$userEmail]")
        emitter.send(dummyEvent)

        notificationRepository.findByToAndIsRead(user, false).forEach {
            val event = SseEmitter
                .event()
                .id("${userEmail}_${it.notificationType}_${it.id}")
                .name("${it.notificationType}")
                .data(NotificationResponseDto.from(it))
            emitter.send(event)
        }
        return emitter
    }

    override fun sendEvent(
        userEmail: String,
        notification: Notification?,
    ) {
        notification ?: return

        val emitter = sseRepository.get(notification.to.email)
            ?: return

        val event = SseEmitter
            .event()
            .id("${userEmail}_${notification.notificationType}_${notification.id}")
            .name("${notification.notificationType}")
            .data(NotificationResponseDto.from(notification))
        emitter.send(event)
    }

    override fun testSend() {
        // 이벤트 이미터 죄다 가져오기
        val emitters = sseRepository.getAllSseList()
        val nowTime = System.currentTimeMillis()
        emitters.forEach {
            val event = SseEmitter
                .event()
                .id(nowTime.toString())
                .name("sse_test")
                .data("test_$nowTime")
            it.send(event)
        }
    }
}
