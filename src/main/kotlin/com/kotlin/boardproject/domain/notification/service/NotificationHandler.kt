package com.kotlin.boardproject.domain.notification.service

import com.kotlin.boardproject.domain.notification.domain.Notification
import com.kotlin.boardproject.domain.notification.repository.NotificationRepository
import com.kotlin.boardproject.domain.notification.repository.SseRepository
import com.kotlin.boardproject.global.event.NotificationEvent
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
@Async("asyncExecutor")
@Transactional(propagation = Propagation.REQUIRES_NEW)
class NotificationHandler(
    private val sseRepository: SseRepository,
    private val notificationRepository: NotificationRepository,
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun createNotification(
        notificationEvent: NotificationEvent,
    ) {
        notificationRepository.save(
            Notification(
                from = notificationEvent.fromUser,
                to = notificationEvent.toUser,
                content = notificationEvent.message,
                url = notificationEvent.url,
                notificationType = notificationEvent.notificationType,
            ),
        )
        // sseRepository.
    }
}
