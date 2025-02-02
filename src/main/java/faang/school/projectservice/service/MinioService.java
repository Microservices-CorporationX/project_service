package faang.school.projectservice.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;

    public void uploadFile(ByteArrayInputStream fileInputStream, String prefix, String contentType, Long fileSize) {
        PutObjectArgs args = PutObjectArgs.builder()
                .contentType(contentType)
                .object( prefix)
                .bucket("corpbucket")
                .stream(fileInputStream, fileSize, -1)
                .build();
        try {
            minioClient.putObject(args);
        } catch (Exception e) {
            log.error("Failed to minioClient.putObject(args)", e);
            throw new RuntimeException("Failed to put object (minio)");
        }
    }

    public void removeFile(String key) {
        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket("corbbucket")
                .object(key)
                .build();
        try {
            minioClient.removeObject(args);
        } catch (Exception e) {
            log.error("Failed to minioClient.removeObject(args)", e);
            throw new RuntimeException("Failed to remove object (minio)");
        }
    }
}
