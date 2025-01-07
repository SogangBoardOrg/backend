package com.kotlin.boardproject.global.event

import org.springframework.context.ApplicationEventPublisher

class SpringEventPublisher(
    private val eventPublisher: ApplicationEventPublisher,
) {

    fun publishEvent(event: Event) {
        eventPublisher.publishEvent(event)
    }
}
