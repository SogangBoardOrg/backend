package com.kotlin.boardproject.common.exception

import com.kotlin.boardproject.common.enums.ErrorCode

class OAuthProviderMissMatchException(log: String) : BizException(ErrorCode.PROVIDER_MISS_MATCH, log)
