package com.kotlin.boardproject.service

import com.kotlin.boardproject.dto.comment.CreateCommentResponseDto
import com.kotlin.boardproject.dto.notification.GetNotificationsResponseDto
import org.springframework.stereotype.Service

@Service
interface NotificationService {

    fun getNotifications(
        email: String,
    ): GetNotificationsResponseDto

    fun deleteNotificationByEmailAndNotificationId(
        email: String,
        notificationId: Long,
    )

    fun deleteAllUnreadNotificationByEmail(
        email: String,
    )

    // TODO: 댓글 알림을 생성하는 메서드
    fun createNotification(
        email: String,
        createCommentResponseDto: CreateCommentResponseDto,
    )
}
