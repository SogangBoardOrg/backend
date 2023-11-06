package com.kotlin.boardproject.global.handler

import com.kotlin.boardproject.global.enums.ErrorCode
import com.kotlin.boardproject.global.exception.BizException
import com.kotlin.boardproject.global.exception.InternalServiceException
import com.kotlin.boardproject.global.util.log
import com.kotlin.boardproject.global.dto.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(value = [BizException::class])
    fun handlingBizException(bizException: BizException): ResponseEntity<ApiResponse<String>> {
        log.error(bizException.log)
        return ResponseEntity.status(bizException.errorCode.code)
            .body(ApiResponse.fail(bizException.errorCode.message))
    }

    @ExceptionHandler(
        value = [
            IllegalArgumentException::class,
            MethodArgumentTypeMismatchException::class,
            HttpRequestMethodNotSupportedException::class,
        ],
    )
    fun handlingBizException(exception: Exception): ResponseEntity<ApiResponse<String>> {
        log.error(exception.message)
        return ResponseEntity.status(ErrorCode.CONDITION_NOT_FULFILLED.code)
            .body(ApiResponse.fail(ErrorCode.CONDITION_NOT_FULFILLED.message))
    }

    @ExceptionHandler(value = [InternalServiceException::class])
    fun handlingInternalServiceException(internalServiceException: InternalServiceException):
        ResponseEntity<ApiResponse<String>> {
        log.error(internalServiceException.log)

        return ResponseEntity.status(internalServiceException.errorCode.code)
            .body(ApiResponse.error(internalServiceException.errorCode.message))
    }

    @ExceptionHandler(value = [Exception::class])
    fun handlingException(exception: Exception): ResponseEntity<ApiResponse<String>> {
        log.error(exception.message)

        if (exception is AccessDeniedException) {
            return ResponseEntity.status(ErrorCode.FORBIDDEN.code)
                .body(ApiResponse.fail(ErrorCode.FORBIDDEN.message))
        }

        return ResponseEntity.status(ErrorCode.SERVER_ERROR.code)
            .body(ApiResponse.error(ErrorCode.SERVER_ERROR.message))
    }
}
