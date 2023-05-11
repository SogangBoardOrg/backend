package com.kotlin.boardproject.dto.notification

data class GetNotificationsResponseDto(
    var notificationCount: Int,
    var notifications: List<NotificationResponseDto>,
)
