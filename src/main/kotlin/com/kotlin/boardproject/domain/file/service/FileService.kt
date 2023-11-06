package com.kotlin.boardproject.domain.file.service

import org.springframework.stereotype.Service

@Service
interface FileService {
    suspend fun geneatePreSingedUrl(
        userEmail: String,
    ): String
}
