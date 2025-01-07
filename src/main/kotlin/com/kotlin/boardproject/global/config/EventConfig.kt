package com.kotlin.boardproject.global.config

import com.kotlin.boardproject.global.event.SpringEventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EventConfig {

    @Bean
    fun eventPublisher(eventPublisher: ApplicationEventPublisher): SpringEventPublisher {
        return SpringEventPublisher(eventPublisher)
    }
}
