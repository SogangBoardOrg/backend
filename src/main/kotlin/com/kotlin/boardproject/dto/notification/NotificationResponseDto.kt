package com.kotlin.boardproject.dto.notification

import com.kotlin.boardproject.common.enums.NotificationType
import com.kotlin.boardproject.model.Notification
import java.time.LocalDateTime

class NotificationResponseDto(
    val id: Long,
    val from: String,
    val url: String,
    val content: String,
    val notificationType: NotificationType,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(
            notification: Notification,
        ): NotificationResponseDto {
            return NotificationResponseDto(
                id = notification.id!!,
                from = notification.from.nickname,
                url = notification.url,
                content = notification.content,
                notificationType = notification.notificationType,
                createdAt = notification.createdAt!!,
            )
        }
    }
}
