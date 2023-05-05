package com.kotlin.boardproject.service

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
interface FileService {
    suspend fun uploadFile(
        file: MultipartFile,
    ): String
}
