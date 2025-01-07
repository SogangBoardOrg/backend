package com.kotlin.boardproject.global.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlin.boardproject.global.config.properties.AppProperties
import com.kotlin.boardproject.global.util.log
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.TimeUnit

const val PASSWORD_VERIFICATION_MINUTE = 5
const val RANKING = "ranking"

@Repository
class RedisRepository(
    private val redisTemplate: StringRedisTemplate,
    private val appProperties: AppProperties,
    private val objectMapper: ObjectMapper,
) {
    fun getRefreshTokenByEmail(email: String): String? {
        return redisTemplate.opsForValue().get(email)
    }

    fun setRefreshTokenByEmail(email: String, refreshToken: String) {
        redisTemplate.opsForValue()
            .set(email, refreshToken, appProperties.auth.refreshTokenExpiry, TimeUnit.MILLISECONDS)
    }

    fun setEventEmitter(email: String, emitter: SseEmitter) {
        log.info(serializeEmitter(emitter))
        redisTemplate.opsForValue()
            .set("eventEmitter_$email", serializeEmitter(emitter), 10 * 60 * 1000)
    }

    private fun serializeEmitter(emitter: SseEmitter): String {
        return objectMapper.writeValueAsString(emitter)
    }

    fun getAllEventEmitter(): List<SseEmitter> {
        return redisTemplate.keys("eventEmitter_*")
            .mapNotNull { deserializeEmitter(redisTemplate.opsForValue().get(it)) }
    }

    fun getEventEmitterByEmail(email: String): SseEmitter? {
        // 이거 아님
        return deserializeEmitter(redisTemplate.opsForValue().get("eventEmitter_$email"))
    }

    private fun deserializeEmitter(emitter: String?): SseEmitter? {
        return emitter?.let { objectMapper.readValue(emitter, SseEmitter::class.java) }
    }

    fun removeEventEmitterByEmail(userEmail: String) {
        redisTemplate.delete("eventEmitter_$userEmail")
    }

    fun setPasswordVerification(code: String, email: String) {
        redisTemplate.opsForValue()
            .set(code, email, PASSWORD_VERIFICATION_MINUTE.toLong(), TimeUnit.MINUTES)
    }

    fun getEmailByCode(code: String): String? {
        return redisTemplate.opsForValue()
            .get(code)
    }

    fun removePasswordVerification(code: String) {
        redisTemplate.delete(code)
    }

    fun setPostLike(postId: Long, userEmail: String) {
        redisTemplate.opsForSet().add("post_like:$postId", userEmail)
    }

    fun cancelPostLike(postId: Long, userEmail: String) {
        redisTemplate.opsForSet().remove("post_like:$postId", userEmail)
    }

    fun userLikesPost(postId: Long, userEmail: String): Boolean {
        return redisTemplate.opsForSet().isMember("post_like:$postId", userEmail)!!
    }

    fun getPostLikeCount(postId: Long): Int {
        return redisTemplate.opsForSet().size("post_like:$postId")!!.toInt()
    }

    fun getPostLikeMap(posts: List<Long>): Map<Long, Int> {
        return posts.associateWith { redisTemplate.opsForSet().size("post_like:$it")!!.toInt() }
    }
}
