package com.kotlin.boardproject.global.exception

import com.kotlin.boardproject.global.enums.ErrorCode

class OAuthProviderMissMatchException(log: String) : BizException(ErrorCode.PROVIDER_MISS_MATCH, log)
