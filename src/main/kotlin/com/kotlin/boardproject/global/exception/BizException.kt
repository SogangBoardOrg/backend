package com.kotlin.boardproject.global.exception

import com.kotlin.boardproject.global.enums.ErrorCode

open class BizException(val errorCode: ErrorCode, val log: String) : RuntimeException()
