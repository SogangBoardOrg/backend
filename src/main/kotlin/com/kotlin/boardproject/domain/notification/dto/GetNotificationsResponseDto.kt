package com.kotlin.boardproject.domain.notification.dto

data class GetNotificationsResponseDto(
    var notificationCount: Int,
    var notifications: List<NotificationResponseDto>,
)
