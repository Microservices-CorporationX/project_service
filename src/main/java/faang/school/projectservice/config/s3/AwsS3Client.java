package faang.school.projectservice.config.s3;

import com.amazonaws.auth.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsS3Client {
    @Value("${services.s3.accessKey}")
    private String accessKey;
    @Value("${services.s3.secretKey}")
    private String accessSecretKey;
    @Value("${services.s3.region}")
    private String region;

    @Bean
    public AmazonS3 s3Config(){
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, accessSecretKey);

        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(region)
                .build();
    }
}
