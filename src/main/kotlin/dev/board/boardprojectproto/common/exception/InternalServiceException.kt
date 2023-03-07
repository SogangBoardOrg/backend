package dev.board.boardprojectproto.common.exception

import dev.board.boardprojectproto.common.enums.ErrorCode

open class InternalServiceException(val errorCode: ErrorCode, val log: String) : RuntimeException()
