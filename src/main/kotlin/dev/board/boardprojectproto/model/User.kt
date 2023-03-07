package dev.board.boardprojectproto.model

import dev.board.boardprojectproto.auth.ProviderType
import dev.board.boardprojectproto.common.enums.CurrentStatus
import dev.board.boardprojectproto.common.enums.Role
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
    var susPendedTime: LocalDateTime? = null,
) : BaseEntity()
