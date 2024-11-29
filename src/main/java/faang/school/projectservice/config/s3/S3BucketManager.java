package faang.school.projectservice.config.s3;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3BucketManager {
    private final S3Client s3Client;
    private final MinioConfigProperties minioConfigProperties;

    @PostConstruct
    public void checkBucketExists() {
        String bucketName = minioConfigProperties.getBucketName();
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
        } catch (NoSuchBucketException e) {
            log.warn("Bucket '{}' doesn't exist. Creating new bucket", bucketName);
            try {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
                log.info("Bucket '{}' successfully created", bucketName);
            } catch (S3Exception bucketCreateException) {
                log.error("Failed to create bucket '{}': {}", bucketName, bucketCreateException.awsErrorDetails().errorMessage());
                throw bucketCreateException;
            }
        }
    }
}