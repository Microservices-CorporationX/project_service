package faang.school.projectservice.config.s3;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "services.s3")
public class S3ConfigProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}