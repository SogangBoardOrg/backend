package com.kotlin.boardproject.dto.notification

import com.kotlin.boardproject.common.enums.NotificationType
import java.util.UUID

data class NotificationDto(
    val toUserId: UUID,
    val url: String,
    val message: String,
    val notificationType: NotificationType,
)
