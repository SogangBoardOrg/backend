package dev.board.boardprojectproto.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Comment(
    var content: String,
    // val author : Long,
    val isAnon: Boolean,

    // @ManyToOne(fetch = FetchType.LAZY)
    // val postId: Long?,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) : BaseEntity()
