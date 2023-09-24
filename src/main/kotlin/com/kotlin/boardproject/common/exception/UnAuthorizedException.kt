package com.kotlin.boardproject.common.exception

import com.kotlin.boardproject.common.enums.ErrorCode

class UnAuthorizedException(
    errorCode: ErrorCode,
    log: String,
) : BizException(errorCode, log)
