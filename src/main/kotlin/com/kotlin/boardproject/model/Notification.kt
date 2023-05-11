package com.kotlin.boardproject.model

import javax.persistence.*

@Entity
class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val to: User,

    val url: String,

    val message: String,

    var isRead: Boolean = false,
) : BaseEntity() {

    fun read() {
        isRead = true
    }
}
