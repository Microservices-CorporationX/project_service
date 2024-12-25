package faang.school.projectservice.service;

import faang.school.projectservice.config.s3.MinioProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageService {
    private final S3Client s3Client;
    private final MinioProperties minioProperties;

    public void uploadResource(MultipartFile file, String key) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(minioProperties.bucketName())
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        try {
            RequestBody requestBody = RequestBody.fromBytes(file.getBytes());
            s3Client.putObject(putObjectRequest, requestBody);
            log.info("File '{}' uploaded successfully", file.getOriginalFilename());
        } catch (IOException ex) {
            throw new IllegalStateException(
                    String.format("An error occurred while uploading file '%s'.", file.getOriginalFilename()), ex);
        }
    }

    public byte[] downloadResource(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(minioProperties.bucketName())
                .key(key)
                .build();

        byte[] result = s3Client.getObjectAsBytes(getObjectRequest).asByteArray();
        log.info("GetObjectRequest by key '{}' created successfully.", key);

        return result;
    }

    public void deleteResource(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(minioProperties.bucketName())
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
        log.info("File with key '{}' deleted successfully.", key);
    }
}