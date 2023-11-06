package com.kotlin.boardproject.domain.notification.domain

import com.kotlin.boardproject.global.enums.NotificationType
import com.kotlin.boardproject.global.domain.BaseEntity
import com.kotlin.boardproject.domain.user.domain.User
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id")
    val from: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id")
    val to: User,

    val url: String,

    val content: String,

    @Enumerated(EnumType.STRING)
    val notificationType: NotificationType = NotificationType.COMMENT,

    var isRead: Boolean = false,
) : BaseEntity() {

    fun read() {
        isRead = true
    }
}
