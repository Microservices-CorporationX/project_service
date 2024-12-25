package faang.school.projectservice.config.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class S3ClientConfig {
    private final MinioProperties minioProperties;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = createAwsCredentials();

        return S3Client.builder()
                .endpointOverride(URI.create(minioProperties.endpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region((Region.US_EAST_1))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    @Bean
    public S3AsyncClient s3AsyncClient() {
        AwsBasicCredentials credentials = createAwsCredentials();

        return S3AsyncClient.builder()
                .endpointOverride(URI.create(minioProperties.endpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.US_EAST_1)
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    private AwsBasicCredentials createAwsCredentials() {
        return AwsBasicCredentials.create(minioProperties.accessKey(), minioProperties.secretKey());
    }
}