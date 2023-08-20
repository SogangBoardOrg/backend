package com.kotlin.boardproject.repository.common

import org.springframework.stereotype.Repository
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@Repository
class SseRepositoryInMemoryImpl(
    private val sseEmitterMap: MutableMap<String, SseEmitter> = ConcurrentHashMap(),
) : SseRepository {
    override fun put(key: String, sseEmitter: SseEmitter) {
        sseEmitterMap[key] = sseEmitter
    }

    override fun get(key: String): SseEmitter? {
        return sseEmitterMap[key]
    }

    override fun remove(key: String) {
        sseEmitterMap.remove(key)
    }

    override fun getListByKeyPrefix(keyPrefix: String): List<SseEmitter> {
        return sseEmitterMap.filterKeys { it.startsWith(keyPrefix) }.values.toList()
    }

    override fun getKeyListByKeyPrefix(keyPrefix: String): List<String> {
        return sseEmitterMap.filterKeys { it.startsWith(keyPrefix) }.keys.toList()
    }

    override fun getAllSseList(): List<SseEmitter> {
        return sseEmitterMap.values.toList()
    }
}
