package faang.school.projectservice.service.google.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.gson.GsonFactory;
import faang.school.projectservice.config.google.GoogleProperties;
import faang.school.projectservice.jpa.JpaDataStoreFactory;
import faang.school.projectservice.repository.GoogleTokenRepository;
import faang.school.projectservice.service.google.utils.GoogleCalendarUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static faang.school.projectservice.service.google.utils.GoogleCalendarUtils.createHttpTransport;

@Component
@Slf4j
@AllArgsConstructor
@Getter
public class GoogleCalendarAuth {
    private GoogleProperties properties;
    private final GoogleTokenRepository googleTokenRepository;

    public GoogleClientSecrets getClientSecrets() throws IOException {
        String filePath = properties.getCredentialsFilePath();
        InputStream inputStream = GoogleCalendarUtils.class.getResourceAsStream(filePath);
        if (inputStream == null) {
            log.error("File not found at path {}", filePath);
            throw new FileNotFoundException("file not found: " + filePath);
        }
        return GoogleClientSecrets.load(new GsonFactory(), new InputStreamReader(inputStream));
    }

    public GoogleAuthorizationCodeFlow getCodeFlow(GoogleClientSecrets clientSecrets) throws IOException {
        return new GoogleAuthorizationCodeFlow.Builder(
                createHttpTransport(), new GsonFactory(), clientSecrets,
                properties.getScopes())
                .setDataStoreFactory(new JpaDataStoreFactory(googleTokenRepository))
                .setAccessType(properties.getAccessType())
                .build();
    }

    public String getAuthorizationUrl(Long userId, Long eventId) throws IOException {
        GoogleClientSecrets googleClientSecrets = getClientSecrets();
        GoogleAuthorizationCodeFlow codeFlow = getCodeFlow(googleClientSecrets);
        return codeFlow.newAuthorizationUrl()
                .setRedirectUri(properties.getRedirectUrl())
                .setState(userId + "-" + eventId)
                .build();
    }
}