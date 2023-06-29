package com.kotlin.boardproject.repository

import com.kotlin.boardproject.model.Notification
import com.kotlin.boardproject.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByToAndIsRead(to: User, isRead: Boolean): List<Notification>

    fun findByToAndIdAndIsRead(to: User, id: Long, isRead: Boolean): Notification?
}
