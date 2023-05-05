package com.kotlin.boardproject.service

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.smithy.kotlin.runtime.content.ByteStream
import com.kotlin.boardproject.common.util.log
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class FileServiceImpl(
    @Value("\${aws.credentials.access-key}")
    private val accessKey: String,

    @Value("\${aws.credentials.secret-key}")
    private val secretKey: String,

    @Value("\${aws.s3.bucket}")
    private val bucketName: String,

    @Value("\${aws.region}")
    private val S3region: String,
) : FileService {
    override suspend fun uploadFile(
        file: MultipartFile,
    ): String {
        val fileName = "static/${UUID.randomUUID()}-${file.originalFilename}"
        log.info("fileName: $fileName")

        S3Client {
            region = S3region
            credentialsProvider = StaticCredentialsProvider {
                accessKeyId = accessKey
                secretAccessKey = secretKey
            }
        }.use {
            log.info("fileName: $fileName")
            it.putObject {
                this.bucket = bucketName
                this.key = fileName
                this.body = ByteStream.fromBytes(file.bytes)
            }
        }

        return "http://$bucketName.s3.$S3region.amazonaws.com/$fileName"
    }
}
