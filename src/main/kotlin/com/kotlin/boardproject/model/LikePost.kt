package com.kotlin.boardproject.model

import com.kotlin.boardproject.common.enums.BlackReason
import javax.persistence.*

@Entity
class LikePost(
    @Id
    @GeneratedValue
    @Column(name = "like_post_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    val post: BasePost,
) : BaseEntity()
