package com.kotlin.boardproject.model

import com.kotlin.boardproject.common.enums.BlackReason
import javax.persistence.*

@Entity
class BlackComment(
    @Id
    @GeneratedValue
    @Column(name = "black_post_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    val comment: Comment,

    @Enumerated(EnumType.STRING)
    val blackReason: BlackReason,
) : BaseEntity()