package com.kotlin.boardproject.service

import com.kotlin.boardproject.dto.notification.GetNotificationsResponseDto
import com.kotlin.boardproject.dto.notification.NotificationCreateDto
import com.kotlin.boardproject.model.Notification
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
