package com.kotlin.boardproject.domain.notification.service

import com.kotlin.boardproject.domain.notification.dto.GetNotificationsResponseDto
import com.kotlin.boardproject.domain.notification.dto.NotificationCreateDto
import com.kotlin.boardproject.domain.notification.domain.Notification
import org.springframework.stereotype.Service

@Service
interface NotificationService {

    fun getNotifications(
        email: String,
    ): GetNotificationsResponseDto

    fun readNotificationByEmailAndNotificationId(
        email: String,
        notificationId: Long,
    )

    fun readAllUnreadNotificationByEmail(
        email: String,
    )

    fun createNotification(
        notificationCreateDto: NotificationCreateDto,
    ): Notification?
}
