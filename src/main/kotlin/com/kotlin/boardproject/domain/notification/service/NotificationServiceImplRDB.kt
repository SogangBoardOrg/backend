package com.kotlin.boardproject.domain.notification.service

import com.kotlin.boardproject.domain.notification.domain.Notification
import com.kotlin.boardproject.domain.notification.dto.GetNotificationsResponseDto
import com.kotlin.boardproject.domain.notification.dto.NotificationCreateDto
import com.kotlin.boardproject.domain.notification.dto.NotificationResponseDto
import com.kotlin.boardproject.domain.notification.repository.NotificationRepository
import com.kotlin.boardproject.domain.user.repository.UserRepository
import com.kotlin.boardproject.global.exception.EntityNotFoundException
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
