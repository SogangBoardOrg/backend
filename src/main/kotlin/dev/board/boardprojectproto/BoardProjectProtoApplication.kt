package dev.board.boardprojectproto

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan
class BoardProjectProtoApplication

fun main(args: Array<String>) {
    runApplication<BoardProjectProtoApplication>(*args)
}
