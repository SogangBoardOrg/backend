package com.kotlin.boardproject.global.dto

import com.fasterxml.jackson.annotation.JsonInclude

private const val SUCCESS = "success"
private const val FAIL = "fail"
private const val ERROR = "error"

@JsonInclude(JsonInclude.Include.NON_NULL)
class ApiResponse<T> private constructor(
    val status: String,
    val data: T? = null,
    val message: String? = null,
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(SUCCESS, data)
        }

        fun <T> fail(reason: T): ApiResponse<T> {
            return ApiResponse(FAIL, reason)
        }

        fun <T> error(message: String): ApiResponse<T> {
            return ApiResponse(ERROR, message = message)
        }
    }
}
