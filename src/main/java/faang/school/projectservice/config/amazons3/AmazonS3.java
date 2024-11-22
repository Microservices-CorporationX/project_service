package faang.school.projectservice.config.amazons3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3 {

    @Value("&{services.s3.accessKey}")
    private String accessKey;

    @Value("&{services.s3.secretKey}")
    private String secretKey;

    @Bean
    public com.amazonaws.services.s3.AmazonS3 s3Client() {
    BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
    return AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            .build();
    }
}
