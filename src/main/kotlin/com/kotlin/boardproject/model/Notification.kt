package com.kotlin.boardproject.model

import com.kotlin.boardproject.common.enums.NotificationType
import javax.persistence.*

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
