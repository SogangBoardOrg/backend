package com.kotlin.boardproject.model

import javax.persistence.*

@Entity
class Comment(
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var content: String,

    val isAnon: Boolean,

    // var showStatus: ,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    val postId: BasePost,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val writer: User,
) : BaseEntity()
