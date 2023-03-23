package com.kotlin.boardproject.model

import com.kotlin.boardproject.auth.ProviderType
import com.kotlin.boardproject.common.enums.CurrentStatus
import com.kotlin.boardproject.common.enums.Role
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
class User(
    @Id
    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    var id: UUID? = null,

    @Column(name = "email", unique = true, columnDefinition = "VARCHAR(30)")
    var email: String,

    @Column(name = "username", unique = true, columnDefinition = "VARCHAR(100)")
    var username: String,

    @Column(name = "password", columnDefinition = "VARCHAR(100)")
    var password: String = "NO_PASSWORD",

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    var role: Role = Role.ROLE_NEWBIE,

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type")
    var providerType: ProviderType,

    @Column(name = "provider_id")
    var providerId: String? = null,

    // 현재 상태 -> 정지인지 뭐인지 등등
    @Enumerated(EnumType.STRING)
    @Column(name = "current_status")
    var currentStatus: CurrentStatus = CurrentStatus.NORMAL,
    // 정지된 날짜
    var suspendedTime: LocalDateTime? = null,

    // 프로필 이미지
    var profileImageUrl: String? = null,

    // 쓴 글 목록
    @OneToMany(mappedBy = "writer")
    val postList: MutableList<BasePost> = mutableListOf(),

    // 쓴 댓글 목록
    @OneToMany(mappedBy = "writer")
    val commentList: MutableList<BasePost> = mutableListOf(),

    @OneToMany(mappedBy = "user")
    val scrapPostList: MutableList<ScrapPost> = mutableListOf(),

) : BaseEntity() {
    fun encodePassword(encodedPassword: String) {
        password = encodedPassword
    }
}
