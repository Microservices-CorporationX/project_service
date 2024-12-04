package faang.school.projectservice.config.calendar;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.io.InputStreamReader;


@Configuration
@Slf4j
public class GoogleClientSecretsConfig {
    @Bean
    public GoogleClientSecrets googleClientSecrets() {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("credentials/google_credentials.json");
            return GoogleClientSecrets.load(GsonFactory.getDefaultInstance(), new InputStreamReader(is));
        } catch (Exception e) {
            log.error("Unable to load Google credentials from JSON");
            return new GoogleClientSecrets();
        }
    }
}
