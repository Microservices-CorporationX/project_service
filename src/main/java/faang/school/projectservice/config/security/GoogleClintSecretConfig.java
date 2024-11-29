package faang.school.projectservice.config.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

@Component
public class GoogleClintSecretConfig {

    @Bean
    public GoogleClientSecrets googleClientSecrets() {
        try {


            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("client_secret_523144599976-otpi6d9avis6olm8j2bmplok654vkeru.apps.googleusercontent.com (1).json");
            return GoogleClientSecrets.load(GsonFactory.getDefaultInstance(), new InputStreamReader(is));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load Google credentials from JSON", e);
        }
    }
}
