package faang.school.projectservice.config.amazons3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AmazonS3Config {

    @Value("${services.s3.accessKey}")
    private String accessKey;

    @Value("${services.s3.secretKey}")
    private String secretKey;

    @Value("${services.s3.region}")
    private String region;

    @Value("${services.s3.endpoint}")
    private String endpoint;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Bean
    public AmazonS3 s3Client() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .build();

        createDefaultBucketIfNotExists(s3Client, bucketName);
        return s3Client;
    }

    private void createDefaultBucketIfNotExists(AmazonS3 s3Client, String bucketName) {
        if (!s3Client.doesBucketExistV2(bucketName)) {
            s3Client.createBucket(bucketName);
            log.info("Bucket created successfully: {}", bucketName);
        } else {
            log.warn("Bucket already exists: {} ", bucketName);
        }
    }
}
