package com.kotlin.boardproject.domain.notification.dto

import com.kotlin.boardproject.domain.notification.domain.Notification
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.global.enums.NotificationType

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
