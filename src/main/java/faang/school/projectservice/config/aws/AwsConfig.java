package faang.school.projectservice.config.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.ProcessCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Builder;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

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

//    private final AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
    @Bean
    public AmazonS3 amazonS3() {

        System.out.println(accessKey);
        System.out.println(secretKey);
        System.out.println(region);
        System.out.println(endpoint);
        System.out.println(bucketName);

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
