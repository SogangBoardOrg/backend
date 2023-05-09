package com.kotlin.boardproject.service

import com.kotlin.boardproject.repository.NotificationRepository
import org.springframework.stereotype.Service

@Service
class NotificationServiceImpl(
    private val notificationRepository: NotificationRepository,
) : NotificationService
