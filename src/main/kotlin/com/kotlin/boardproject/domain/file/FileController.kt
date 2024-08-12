package com.kotlin.boardproject.domain.file

import com.kotlin.boardproject.domain.file.service.FileService
import com.kotlin.boardproject.global.annotation.LoginUser
import com.kotlin.boardproject.global.dto.ApiResponse
import kotlinx.coroutines.runBlocking
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.GetMapping
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

    @GetMapping("/presign")
    fun preSignUrl(
        @LoginUser loginUser: User,
    ): ApiResponse<String> {
        return runBlocking {
            val preSingedUrl = fileService.geneatePreSingedUrl(loginUser.username)
            ApiResponse.success(preSingedUrl)
        }
    }

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
