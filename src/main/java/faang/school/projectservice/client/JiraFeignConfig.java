package faang.school.projectservice.client;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class JiraFeignConfig {

    @Value("${jira.username}")
    private String username;

    @Value("${jira.password}")
    private String password;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder()
                    .encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            template.header("Authorization", "Basic " + encodedAuth);
            template.header("Content-Type", "application/json");
        };
    }
}