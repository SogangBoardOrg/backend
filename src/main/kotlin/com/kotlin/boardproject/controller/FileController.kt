package com.kotlin.boardproject.controller

import com.kotlin.boardproject.auth.LoginUser
import com.kotlin.boardproject.common.util.log
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class FileController {

    @GetMapping("/users")
    fun test(): String {
        return "hi"
    }

    @GetMapping("/test")
    fun test(@LoginUser loginUser: User): String {
        log.info(loginUser.username)

        println("안녕하세요")
        return "hello"
    }
}
