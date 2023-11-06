package com.kotlin.boardproject.global.exception

import com.kotlin.boardproject.global.enums.ErrorCode

class UnAuthorizedException(
    errorCode: ErrorCode,
    log: String,
) : BizException(errorCode, log)
