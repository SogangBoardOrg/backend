package com.kotlin.boardproject.dto.notification

import com.kotlin.boardproject.model.Notification
import java.time.LocalDateTime

class NotificationResponseDto(
    val id: Long,
    val title: String,
    val url: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(
            notification: Notification,
        ): NotificationResponseDto {
            return NotificationResponseDto(
                id = notification.id!!,
                title = notification.title,
                url = notification.url,
                createdAt = notification.createdAt!!,
            )
        }
    }
}