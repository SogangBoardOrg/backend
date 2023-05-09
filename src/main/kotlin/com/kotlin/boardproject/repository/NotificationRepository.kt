package com.kotlin.boardproject.repository

import com.kotlin.boardproject.model.Notification
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, Long>
