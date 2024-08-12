package com.kotlin.boardproject.domain.file.service

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.Headers
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.util.IOUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.util.Date
import java.util.UUID

@Service
class FileServiceImpl(
    @Value("\${aws.credentials.access-key}")
    private val accessKey: String,

    @Value("\${aws.credentials.secret-key}")
    private val secretKey: String,

    @Value("\${aws.s3.bucket}")
    private val bucketName: String,

    private val s3Client: AmazonS3,
) : FileService {
    override suspend fun geneatePreSingedUrl(
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

    override suspend fun uploadFile(
        file: MultipartFile,
    ): String {
        val fileName = generateFileName()
        val bytes = IOUtils.toByteArray(file.inputStream)
        val objMeta = ObjectMetadata()
        objMeta.contentLength = bytes.size.toLong()

        val byteArrayIs = ByteArrayInputStream(bytes)

        s3Client.putObject(
            PutObjectRequest(bucketName, fileName, byteArrayIs, objMeta)
                .withCannedAcl(CannedAccessControlList.PublicRead),
        )

        return s3Client.getUrl(bucketName, fileName).toString()
    }
}
