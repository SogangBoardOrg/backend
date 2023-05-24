package com.kotlin.boardproject.service

import com.kotlin.boardproject.common.exception.EntityNotFoundException
import com.kotlin.boardproject.common.util.log
import com.kotlin.boardproject.dto.comment.CreateCommentResponseDto
import com.kotlin.boardproject.dto.notification.GetNotificationsResponseDto
import com.kotlin.boardproject.dto.notification.NotificationResponseDto
import com.kotlin.boardproject.model.Notification
import com.kotlin.boardproject.repository.CommentRepository
import com.kotlin.boardproject.repository.NotificationRepository
import com.kotlin.boardproject.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationServiceImpl(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
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
        email: String,
        createCommentResponseDto: CreateCommentResponseDto,
    ) {
        val comment = commentRepository.findByIdOrNull(createCommentResponseDto.id)
            ?: throw EntityNotFoundException("댓글이 존재하지 않습니다.")
        val post = comment.post
        log.info("post: $post")
        // TODO: 대댓글 애러 고치기
        log.info("post: $comment")

        val toId = comment.parent?.writer?.id ?: comment.post.writer.id

        val user = userRepository.findByIdOrNull(toId)
            ?: throw EntityNotFoundException("사용자가 존재하지 않습니다.")
        // TODO: 여기가 문제인거 같음
        if (user.email == email) return

        val notification = Notification(
            to = user,
            message = "message is being set.",
            url = "/post/${post.id}",
        )
        notificationRepository.save(notification)
    }

    @Transactional
    override fun deleteNotificationByEmailAndNotificationId(
        email: String,
        notificationId: Long,
    ) {
        val user = userRepository.findByEmail(email)
            ?: throw EntityNotFoundException("사용자가 존재하지 않습니다.")

        notificationRepository.findByToAndIdAndIsRead(user, notificationId, false).read()
    }

    @Transactional
    override fun deleteAllUnreadNotificationByEmail(
        email: String,
    ) {
        val user = userRepository.findByEmail(email)
            ?: throw EntityNotFoundException("사용자가 존재하지 않습니다.")

        notificationRepository.findByToAndIsRead(user, false).map {
            it.read()
        }
    }
}
