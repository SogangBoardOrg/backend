package com.kotlin.boardproject.domain.notification.service

import com.kotlin.boardproject.domain.notification.dto.GetNotificationsResponseDto
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
}
