package com.kotlin.boardproject.common.exception

import com.kotlin.boardproject.common.enums.ErrorCode

class ConditionConflictException(errorCode: ErrorCode, log: String) : BizException(
    ErrorCode.CONDITION_NOT_FULFILLED,
    log,
)
