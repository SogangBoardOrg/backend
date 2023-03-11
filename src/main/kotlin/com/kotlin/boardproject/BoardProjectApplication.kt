package com.kotlin.boardproject

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan
class BoardProjectApplication

// TODO: application yml 값 변경하기
// TODO: username 처리 방법 알아보기 -> 랜덤 닉네임 생성?
// TODO: redis refresh token 생성


fun main(args: Array<String>) {
    runApplication<BoardProjectApplication>(*args)
}
