package com.kotlin.boardproject.controller

import com.kotlin.boardproject.dto.common.ApiResponse
import com.kotlin.boardproject.service.FileService
import kotlinx.coroutines.runBlocking
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/file")
class FileController(
    private val fileService: FileService,
) {

    @GetMapping("/upload")
    fun test(
        // @LoginUser loginUser: User,
        // @RequestPart("file", required = false) file: MultipartFile,
    ): ApiResponse<String> {
        return runBlocking {
            val fileUrl = fileService.presingedUrl()
            // url 리턴하기
            ApiResponse.success(fileUrl)
        }
    }
}
