package faang.school.projectservice.config.google;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@ConfigurationProperties("google-service")
public class GoogleProperties {
    private String applicationName;
    private String credentialsFilePath;
    private String calendarId;
    private String redirectUrl;
    private String accessType;
    private List<String> scopes;
}