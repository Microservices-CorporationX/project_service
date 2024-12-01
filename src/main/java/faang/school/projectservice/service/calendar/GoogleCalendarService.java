package faang.school.projectservice.service.calendar;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.calendar.GoogleCalendarToken;
import faang.school.projectservice.repository.GoogleCalendarTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarService {
    private final GoogleClientSecrets clientSecrets;
    private final GoogleCalendarTokenRepository googleCalendarTokenRepository;
    private final UserContext userContext;
    @Value("${google.redirect-uri}")
    private String redirectUri;

    public void getAndSaveToken(String code) {
        try {
            TokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    clientSecrets.getDetails().getTokenUri(),
                    clientSecrets.getDetails().getClientId(),
                    clientSecrets.getDetails().getClientSecret(),
                    code,
                    redirectUri
            ).execute();
            GoogleCalendarToken googleCalendarToken = GoogleCalendarToken.builder().userId(userContext.getUserId())
                    .token(tokenResponse.getAccessToken())
                    .build();
            googleCalendarTokenRepository.save(googleCalendarToken);
        } catch (Exception e) {
            log.error("Exception when saving token", e);
            throw new IllegalStateException("Exception when saving token: " + e.getMessage());
        }
    }
}
