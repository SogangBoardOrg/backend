package com.kotlin.boardproject.dto.notification

import com.kotlin.boardproject.common.enums.NotificationType
import com.kotlin.boardproject.model.Notification
import com.kotlin.boardproject.model.User

data class NotificationCreateDto(
    val fromUser: User,
    val toUser: User,
    val url: String,
    val message: String,
    val notificationType: NotificationType,
) {
    companion object {
        fun toNotification(
            notificationCreateDto: NotificationCreateDto,
        ): Notification {
            return Notification(
                from = notificationCreateDto.fromUser,
                to = notificationCreateDto.toUser,
                url = notificationCreateDto.url,
                content = notificationCreateDto.message,
                notificationType = notificationCreateDto.notificationType,
            )
        }
    }
}
