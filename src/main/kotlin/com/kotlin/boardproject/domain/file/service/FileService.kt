package com.kotlin.boardproject.domain.file.service

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
interface FileService {
    suspend fun geneatePreSingedUrl(
        userEmail: String,
    ): String

    suspend fun uploadFile(file: MultipartFile): String
}
