package com.kotlin.boardproject.domain.comment.domain

import com.kotlin.boardproject.domain.comment.dto.update.UpdateCommentRequestDto
import com.kotlin.boardproject.domain.post.domain.BasePost
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.global.domain.BaseEntity
import com.kotlin.boardproject.global.enums.ErrorCode
import com.kotlin.boardproject.global.enums.PostStatus
import com.kotlin.boardproject.global.exception.ConditionConflictException
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
import javax.persistence.OneToMany

@Entity
class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    var id: Long? = null,

    var content: String,

    var isAnon: Boolean,

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

    @OneToMany(mappedBy = "comment")
    val likeList: MutableList<LikeComment> = mutableListOf(),

    @Enumerated(EnumType.STRING)
    var status: PostStatus = PostStatus.NORMAL,
) : BaseEntity() {

    fun addComment(post: BasePost) {
        post.commentList.add(this)
    }

    fun likeComment(likeComment: LikeComment) {
        this.likeList.add(likeComment)
    }

    fun cancelLikeComment(likeComment: LikeComment) {
        this.likeList.remove(likeComment)
    }

    fun checkWriter(user: User) {
        require(user.id == this.writer.id) {
            throw ConditionConflictException(ErrorCode.CONDITION_NOT_FULFILLED, "해당 댓글의 유저가 아닙니다!")
        }
    }

    fun editComment(updateCommentRequestDto: UpdateCommentRequestDto) {
        this.content = updateCommentRequestDto.content
        this.isAnon = updateCommentRequestDto.isAnon
    }

    fun checkPost(postId: Long) {
        require(this.post.id == postId) {
            throw ConditionConflictException(ErrorCode.CONDITION_NOT_FULFILLED, "해당 댓글의 게시글이 아닙니다!")
        }
    }
}
