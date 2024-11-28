package faang.school.projectservice.service;


import faang.school.projectservice.config.s3.MinioConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageService {
    private final S3AsyncClient s3AsyncClient;
    private final S3Client s3Client;
    private final MinioConfigProperties minioConfigProperties;
    private final ExecutorService cachedThreadPool;


    public void uploadResourceAsync(MultipartFile file, String key) {
        PutObjectRequest putObjectRequest = createPutObjectRequest(file, key);
        AsyncRequestBody asyncRequestBody = createAsyncRequestBody(file);
        log.info("AsyncRequestBody for file '{}' upload created successfully", file.getOriginalFilename());

        CompletableFuture<PutObjectResponse> futureTask = s3AsyncClient.putObject(putObjectRequest, asyncRequestBody);
        try {
            futureTask.join();
        } catch(CompletionException e) {
            throw new RuntimeException(String.format("Failed to upload file: %s", file.getOriginalFilename()), e);
        }
        log.info("File '{}' uploaded successfully", file.getOriginalFilename());
    }

    public void deleteResource(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(minioConfigProperties.getBucketName())
                    .key(key)
                    .build());
        } catch (S3Exception e) {
            throw new RuntimeException(String.format("Failed to delete file with key: %s", key), e);
        }
    }

    private PutObjectRequest createPutObjectRequest(MultipartFile file, String key) {
        return PutObjectRequest.builder()
                .bucket(minioConfigProperties.getBucketName())
                .key(key)
                .contentLength(file.getSize())
                .contentType(file.getContentType())
                .build();
    }

    private AsyncRequestBody createAsyncRequestBody(MultipartFile file) {
        try {
            return AsyncRequestBody.fromInputStream(
                    file.getInputStream(),
                    file.getSize(),
                    cachedThreadPool);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create AsyncRequestBody", e);
        }
    }
}

