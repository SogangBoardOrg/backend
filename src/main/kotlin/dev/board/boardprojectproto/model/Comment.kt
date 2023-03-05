package dev.board.boardprojectproto.model

import jakarta.persistence.*

@Entity
class Comment(
    var content: String,
    // val author : Long,
    val isAnon: Boolean,

    @ManyToOne(fetch = FetchType.LAZY)
    val postId: Long,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseEntity()
