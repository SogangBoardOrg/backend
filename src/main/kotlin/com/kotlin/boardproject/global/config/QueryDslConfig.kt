package com.kotlin.boardproject.global.config

import com.querydsl.jpa.impl.JPAQueryFactory
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QueryDslConfig(
    @PersistenceContext
    private val entityManager: EntityManager,
) {
    @Bean
    fun jpaQueryFactory(): JPAQueryFactory {
        return JPAQueryFactory(entityManager)
    }
}