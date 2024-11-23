package faang.school.projectservice.config.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    @Value("${services.s3.accessKey}")
    private String accessKey;

    @Value("${services.s3.secretKey}")
    private String secretKey;

    @Value("${services.s3.endpoint}")
    private String endpoint;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Bean
    public AmazonS3 amazonS3() {

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

         AmazonS3 amazonS3Client = AmazonS3Client.builder()
                 .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, null))
                 .withCredentials(new AWSStaticCredentialsProvider(credentials))
                 .build();

         if (!amazonS3Client.doesBucketExistV2(bucketName)) {
             amazonS3Client.createBucket(bucketName);
         }

         return amazonS3Client;
    }
}
