package com.kotlin.boardproject.model

import com.kotlin.boardproject.common.enums.PostStautus
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
    val post: BasePost,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val writer: User,

    @Enumerated(EnumType.STRING)
    var status: PostStautus = PostStautus.NORMAL,
) : BaseEntity() {

    fun addComment(post: BasePost) {
        post.commentList.add(this)
    }
}
