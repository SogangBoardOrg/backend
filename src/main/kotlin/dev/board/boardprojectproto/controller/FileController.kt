package dev.board.boardprojectproto.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class FileController {

    @GetMapping("/test")
    fun test(): String {
        println("안녕하세요")
        return "hello"
    }
}
