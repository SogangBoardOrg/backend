package com.kotlin.boardproject.service

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.CannedAccessControlList
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

    override suspend fun presingedUrl(
        // fileName: String,
    ): String {
        val fileName = "static/${UUID.randomUUID()}"

        val req = PutObjectRequest()
            .withCannedAcl(CannedAccessControlList.PublicRead)

        val data = s3Client.generatePresignedUrl(
            bucketName,
            fileName,
            Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7),
            HttpMethod.PUT,
        )

        return data.toString()
    }
}
