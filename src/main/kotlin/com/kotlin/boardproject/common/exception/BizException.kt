package com.kotlin.boardproject.common.exception

import com.kotlin.boardproject.common.enums.ErrorCode

open class BizException(val errorCode: ErrorCode, val log: String) : RuntimeException()
