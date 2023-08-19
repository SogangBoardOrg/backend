package com.kotlin.boardproject.service

import com.kotlin.boardproject.common.exception.EntityNotFoundException
import com.kotlin.boardproject.dto.notification.GetNotificationsResponseDto
import com.kotlin.boardproject.dto.notification.NotificationCreateDto
import com.kotlin.boardproject.dto.notification.NotificationResponseDto
import com.kotlin.boardproject.model.Notification
import com.kotlin.boardproject.repository.NotificationRepository
import com.kotlin.boardproject.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationServiceImplRDB(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
) : NotificationService {

    @Transactional(readOnly = true)
    override fun getNotifications(
        email: String,
    ): GetNotificationsResponseDto {
        val user = userRepository.findByEmail(email)
            ?: throw EntityNotFoundException("사용자가 존재하지 않습니다.")

        val notifications = notificationRepository.findByToAndIsRead(user, false).map {
            NotificationResponseDto.from(it)
        }

        return GetNotificationsResponseDto(
            notificationCount = notifications.size,
            notifications = notifications,
        )
    }

    @Transactional
    override fun createNotification(
        notificationCreateDto: NotificationCreateDto,
    ): Notification? {
        if (notificationCreateDto.fromUser.id == notificationCreateDto.toUser.id) {
            return null
        }

        val notification = NotificationCreateDto.toNotification(notificationCreateDto)

        return notificationRepository.save(notification)
    }

    @Transactional
    override fun readNotificationByEmailAndNotificationId(
        email: String,
        notificationId: Long,
    ) {
        val user = userRepository.findByEmail(email)
            ?: throw EntityNotFoundException("사용자가 존재하지 않습니다.")

        notificationRepository.findByToAndIdAndIsRead(user, notificationId, false)?.read()
            ?: throw EntityNotFoundException("해당 알림이 존재하지 않습니다.")
    }

    @Transactional
    override fun readAllUnreadNotificationByEmail(
        email: String,
    ) {
        val user = userRepository.findByEmail(email)
            ?: throw EntityNotFoundException("사용자가 존재하지 않습니다.")

        notificationRepository.findByToAndIsRead(user, false)
            .map { it.read() }
    }
}
