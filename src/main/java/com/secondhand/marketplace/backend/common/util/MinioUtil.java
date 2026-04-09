package com.secondhand.marketplace.backend.common.util;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class MinioUtil {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    /**
     * Upload file to MinIO
     * @param file MultipartFile
     * @param folder folder name
     * @return file path
     */
    public String uploadFile(MultipartFile file, String folder) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String filename = UUID.randomUUID().toString() + suffix;
        String objectName = folder + "/" + filename;

        // Upload file
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }

        return objectName;
    }

    /**
     * Get presigned URL for file access
     * @param objectName file path in MinIO
     * @param expires expiration time in seconds
     * @return presigned URL
     */
    public String getPresignedUrl(String objectName, int expires) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .method(Method.GET)
                        .expiry(expires, TimeUnit.SECONDS)
                        .build()
        );
    }

    /**
     * Get presigned URL with default expiration (7 days)
     * @param objectName file path in MinIO
     * @return presigned URL
     */
    public String getPresignedUrl(String objectName) throws Exception {
        return getPresignedUrl(objectName, 7 * 24 * 60 * 60);
    }
}
