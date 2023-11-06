package com.kotlin.boardproject.global.exception

import com.kotlin.boardproject.global.enums.ErrorCode

class EntityNotFoundException(log: String) : BizException(ErrorCode.NOT_FOUND_ENTITY, log)
