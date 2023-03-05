package dev.board.boardprojectproto.model

import dev.board.boardprojectproto.auth.ProviderType
import dev.board.boardprojectproto.common.enums.CurrentStatus
import dev.board.boardprojectproto.common.enums.Role
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import java.util.UUID

@Entity
class User(
    var username: String,
    var password: String = "NO_PASSWORD",

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    var role: Role = Role.ROLE_NEWBIE,

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type")
    var providerType: ProviderType,
    // 현재 상태 -> 정지인지 뭐인지 등등
    var currentStatus: CurrentStatus,
    // 정지된 날짜
    var susPendedTime: LocalDateTime?,

    @Id
    @Column(name = "user_id")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    val id: UUID? = null,
) : BaseEntity()
