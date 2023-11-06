package com.kotlin.boardproject.domain.notification.repository

import com.kotlin.boardproject.domain.notification.domain.Notification
import com.kotlin.boardproject.domain.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByToAndIsRead(to: User, isRead: Boolean): List<Notification>

    fun findByToAndIdAndIsRead(to: User, id: Long, isRead: Boolean): Notification?
}
