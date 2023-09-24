package com.kotlin.boardproject.common.exception

import com.kotlin.boardproject.common.enums.ErrorCode

class EntityNotFoundException(log: String) : BizException(ErrorCode.NOT_FOUND_ENTITY, log)
