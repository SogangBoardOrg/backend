package com.kotlin.boardproject.controller

import com.kotlin.boardproject.auth.LoginUser
import com.kotlin.boardproject.dto.common.ApiResponse
import com.kotlin.boardproject.service.FileService
import kotlinx.coroutines.runBlocking
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/file")
class FileController(
    private val fileService: FileService,
) {

    @PostMapping("/upload")
    fun test(
        @LoginUser loginUser: User,
        @RequestPart("file") file: MultipartFile,
    ): ApiResponse<String> {
        println(loginUser.username)
        return runBlocking {
            val fileUrl = fileService.uploadFile(file)
            // url 리턴하기
            ApiResponse.success(fileUrl)
        }
    }
}
