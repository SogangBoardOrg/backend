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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    val parent: Comment? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ancestor_id")
    val ancestor: Comment? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    val post: BasePost,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val writer: User,

    // == 부모 댓글을 삭제해도 후손 댓글은 남아있음 ==//
    @OneToMany(mappedBy = "ancestor")
    val descendentList: MutableList<Comment> = mutableListOf(),

    @Enumerated(EnumType.STRING)
    var status: PostStautus = PostStautus.NORMAL,
) : BaseEntity() {

    fun addComment(post: BasePost) {
        post.commentList.add(this)
    }

    fun joinAncestor(ancestor: Comment?) {
        ancestor?.descendentList?.add(this)
    }
}
