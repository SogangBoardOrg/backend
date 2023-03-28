package com.kotlin.boardproject.model

import javax.persistence.*

@Entity
class ScrapPost(
    @Id
    @GeneratedValue
    @Column(name = "scrap_post_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    val post: BasePost,
) : BaseEntity()
