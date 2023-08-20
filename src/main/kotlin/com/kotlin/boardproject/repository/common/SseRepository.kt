package com.kotlin.boardproject.repository.common

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

interface SseRepository {
    fun put(key: String, sseEmitter: SseEmitter)

    fun get(key: String): SseEmitter?

    fun remove(key: String)

    fun getListByKeyPrefix(keyPrefix: String): List<SseEmitter>

    fun getKeyListByKeyPrefix(keyPrefix: String): List<String>

    fun getAllSseList(): List<SseEmitter>
}
