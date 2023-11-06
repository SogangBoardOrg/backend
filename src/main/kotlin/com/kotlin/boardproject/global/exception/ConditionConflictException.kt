package com.kotlin.boardproject.global.exception

import com.kotlin.boardproject.global.enums.ErrorCode

class ConditionConflictException(errorCode: ErrorCode, log: String) : BizException(
    ErrorCode.CONDITION_NOT_FULFILLED,
    log,
)
