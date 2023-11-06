package com.kotlin.boardproject.domain.post.domain

import com.kotlin.boardproject.domain.comment.domain.Comment
import com.kotlin.boardproject.domain.user.domain.User
import com.kotlin.boardproject.global.domain.BaseEntity
import com.kotlin.boardproject.global.enums.ErrorCode
import com.kotlin.boardproject.global.enums.PostStatus
import com.kotlin.boardproject.global.exception.UnAuthorizedException
import javax.persistence.Column
import javax.persistence.DiscriminatorColumn
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Inheritance
import javax.persistence.InheritanceType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

// 게시판 상관없이 모두 적용되는 속성을 넣는다.
// 테이블 전략은 상속관계 매핑 중에서 조인 전략을 사용

// 유저는 아직
// 좋아요, 댓글 개수는 count query 사용해서 한다.

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "postType")
class BasePost(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    var id: Long? = null,

    var title: String,

    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val writer: User,

    var isAnon: Boolean,

    var commentOn: Boolean,

    @Enumerated(EnumType.STRING) var status: PostStatus = PostStatus.NORMAL,

    @OneToMany(mappedBy = "post") val commentList: MutableList<Comment> = mutableListOf(),

    @OneToMany(mappedBy = "post") val likeList: MutableList<LikePost> = mutableListOf(),

    @OneToMany(mappedBy = "post") val scrapList: MutableList<ScrapPost> = mutableListOf(),

    @ElementCollection var photoList: List<String> = emptyList(),
) : BaseEntity() {
    fun addPost(user: User) {
        // user.postList.add(this)
    }

    fun addLikePost(likePost: LikePost, user: User) {
        this.likeList.add(likePost)
        user.likePostList.add(likePost)
    }

    fun cancelLikePost(likePost: LikePost, user: User) {
        this.likeList.remove(likePost)
        user.likePostList.remove(likePost)
    }

    fun addScrapPost(scrapPost: ScrapPost, user: User) {
        this.scrapList.add(scrapPost)
        user.scrapList.add(scrapPost)
    }

    fun cancelScrapPost(scrapPost: ScrapPost, user: User) {
        this.scrapList.remove(scrapPost)
        user.scrapList.remove(scrapPost)
    }

    fun checkWriter(user: User) {
        require(this.writer == user) { throw UnAuthorizedException(ErrorCode.FORBIDDEN, "해당 글의 주인이 아닙니다.") }
    }

    fun deletePost(user: User) {
        this.status = PostStatus.DELETED
        // user.postList.remove(this)
    }
}
