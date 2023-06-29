package com.kotlin.boardproject.model

import com.kotlin.boardproject.common.enums.PostStatus
import com.kotlin.boardproject.dto.OneCommentResponseDto
import javax.persistence.*

@Entity
class Comment(
    // TODO: 좋아요 양방향 연관관계 구축하기

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
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

    @OneToMany(mappedBy = "comment")
    val likeList: MutableList<LikeComment> = mutableListOf(),

    @Enumerated(EnumType.STRING)
    var status: PostStatus = PostStatus.NORMAL,
) : BaseEntity() {

    fun addComment(post: BasePost) {
        post.commentList.add(this)
    }

    fun joinAncestor(ancestor: Comment?) {
        ancestor?.descendentList?.add(this)
    }

    fun likeComment(likeComment: LikeComment) {
        this.likeList.add(likeComment)
    }

    fun cancelLikeComment(likeComment: LikeComment) {
        this.likeList.remove(likeComment)
    }

    fun toOneCommentResponseDto(): OneCommentResponseDto {
        return OneCommentResponseDto(
            id = this.id!!,
            postId = this.post.id!!,
            content = this.content,
            createdAt = this.createdAt!!,
            updatedAt = this.updatedAt!!,
        )
    }
}
