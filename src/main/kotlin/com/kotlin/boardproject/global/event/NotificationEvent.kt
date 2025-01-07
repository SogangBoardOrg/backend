package com.kotlin.boardproject.global.event

import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.global.enums.NotificationType

class NotificationEvent(
    val fromUser: User,
    val toUser: User,
    val url: String,
    val message: String,
    val notificationType: NotificationType,
) : Event
