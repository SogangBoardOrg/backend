package com.kotlin.boardproject.controller

import com.kotlin.boardproject.common.util.log
import com.kotlin.boardproject.dto.common.ApiResponse
import com.kotlin.boardproject.service.SseEmitters
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import javax.validation.constraints.Positive

@RestController
@RequestMapping("/api/v1")
class SseController(
    private val sseEmitters: SseEmitters,
) {

    @GetMapping("/subscribe", produces = ["text/event-stream"])
    fun subscribe(
        @PathVariable @Positive
        id: Long,
    ): SseEmitter {
        log.info("test")
        val emitter = SseEmitter()

        sseEmitters.add(emitter)

        emitter.send(
            SseEmitter.event()
                .name("connect")
                .data("connected!"),
        )

        return emitter
    }

    @PostMapping("/count")
    fun count(): ApiResponse<String> {
        sseEmitters.count()
        return ApiResponse.success("success")
    }
}
