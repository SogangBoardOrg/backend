package com.kotlin.boardproject.service

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.Headers
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.util.IOUtils
import com.kotlin.boardproject.common.util.log
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.util.*

@Service
class FileServiceImpl(
    @Value("\${aws.credentials.access-key}")
    private val accessKey: String,

    @Value("\${aws.credentials.secret-key}")
    private val secretKey: String,

    @Value("\${aws.s3.bucket}")
    private val bucketName: String,

    @Value("\${aws.region.static}")
    private val S3region: String,

    private val s3Client: AmazonS3,
) : FileService {
    override fun uploadFile(
        file: MultipartFile,
    ): String {
        val fileName = "static/lenna.png"

        log.info("fileName: $fileName")

        val objMeta = ObjectMetadata()

        val bytes = IOUtils.toByteArray(file.inputStream)
        objMeta.contentLength = bytes.size.toLong()

        val byteArrayIs = ByteArrayInputStream(bytes)

        s3Client.putObject(
            PutObjectRequest(bucketName, fileName, byteArrayIs, objMeta)
                .withCannedAcl(CannedAccessControlList.PublicRead),
        )

        return s3Client.getUrl(bucketName, fileName).toString()
    }

    override suspend fun preSingedUrl(
        userEmail: String,
    ): String {
        val fileName = generateFileName() // 여기 유저 이름과
        val generatePreSignedUrlRequest =
            getGeneratePreSignedUrlRequest(bucketName, fileName)

        val data = s3Client.generatePresignedUrl(generatePreSignedUrlRequest)

        return data!!.toString()
    }

    private fun generateFileName(): String =
        "sogang/${UUID.randomUUID()}-${UUID.randomUUID()}"

    private fun getGeneratePreSignedUrlRequest(
        bucket: String,
        fileName: String,
    ): GeneratePresignedUrlRequest {
        val generatePreSignedUrlRequest = GeneratePresignedUrlRequest(bucket, fileName, HttpMethod.PUT)
            .withExpiration(getPreSignedUrlExpiration())
        generatePreSignedUrlRequest.addRequestParameter(
            Headers.S3_CANNED_ACL,
            CannedAccessControlList.PublicRead.toString(),
        )
        return generatePreSignedUrlRequest
    }

    private fun getPreSignedUrlExpiration(): Date {
        val expiration = Date()
        var expTimeMillis = expiration.time
        expTimeMillis += 1000 * 60 * 30
        expiration.time = expTimeMillis
        return expiration
    }
}
