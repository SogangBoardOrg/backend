package com.kotlin.boardproject.domain.user.domain

import com.kotlin.boardproject.domain.post.domain.LikePost
import com.kotlin.boardproject.domain.post.domain.ScrapPost
import com.kotlin.boardproject.global.domain.BaseEntity
import com.kotlin.boardproject.global.enums.CurrentStatus
import com.kotlin.boardproject.global.enums.ProviderType
import com.kotlin.boardproject.global.enums.Role
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class User(
    @Id
    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    var id: UUID? = null,

    @Column(name = "email", unique = true, columnDefinition = "VARCHAR(30)")
    var email: String, // username 과 동의어

    @Column(name = "nickname", unique = true, columnDefinition = "VARCHAR(100)")
    var nickname: String,

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

    @OneToMany(mappedBy = "user")
    val likePostList: MutableList<LikePost> = mutableListOf(),

    @OneToMany(mappedBy = "user")
    val scrapList: MutableList<ScrapPost> = mutableListOf(),

) : BaseEntity() {
    fun encodePassword(encodedPassword: String) {
        password = encodedPassword
    }
}
