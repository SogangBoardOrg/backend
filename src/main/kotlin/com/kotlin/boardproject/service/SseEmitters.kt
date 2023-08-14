package com.kotlin.boardproject.service

import com.kotlin.boardproject.common.util.log
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Consumer

@Component
class SseEmitters {

    private val counter = AtomicLong()
    private val emitters: MutableList<SseEmitter> = CopyOnWriteArrayList()

    fun add(emitter: SseEmitter): SseEmitter? {
        emitters.add(emitter)
        log.info("new emitter added: {}", emitter)
        log.info("emitter list size: {}", emitters.size)
        emitter.onCompletion {
            log.info("onCompletion callback")
            emitters.remove(emitter) // 만료되면 리스트에서 삭제
        }
        emitter.onTimeout {
            log.info("onTimeout callback")
            emitter.complete()
        }
        return emitter
    }

    fun count() {
        val count = counter.incrementAndGet()
        emitters.forEach(
            Consumer { emitter: SseEmitter ->
                emitter.send(
                    SseEmitter.event()
                        .name("count")
                        .data(count),
                )
            },
        )
    }
}
