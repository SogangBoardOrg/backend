package com.kotlin.boardproject.common.enums

enum class Role(
    val code: String,
    val displayName: String,
) {
    ROLE_ADMIN("ROLE_ADMIN", "관리자 권한"),
    ROLE_NEWBIE("ROLE_NEWBIE", "학생증 인증 안한 사용자"),
    ROLE_VERIFIED_USER("ROLE_VERIFIED_USER", "학생증 인증한 사용자"),
    ;

    companion object {
        fun of(code: String): Role {
            return Role.values()
                .find { role -> role.code == code } ?: ROLE_NEWBIE
        }
    }
}
