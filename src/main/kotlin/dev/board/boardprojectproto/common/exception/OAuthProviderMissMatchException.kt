package dev.board.boardprojectproto.common.exception

import dev.board.boardprojectproto.common.enums.ErrorCode

class OAuthProviderMissMatchException(log: String) : BizException(ErrorCode.PROVIDER_MISS_MATCH, log)
