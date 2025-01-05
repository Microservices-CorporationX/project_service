package faang.school.projectservice.config.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services.minio")
public record MinioProperties(String endpoint, String accessKey, String secretKey, String bucketName) {
}