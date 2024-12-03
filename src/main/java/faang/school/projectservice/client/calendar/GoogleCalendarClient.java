package faang.school.projectservice.client.calendar;

import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.AclRule;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarClient {
    private final GoogleClientSecrets clientSecrets;
    @Value("${google.redirect-uri}")
    private String redirectUri;
    @Value("${google.application-name}")
    private String applicationName;

    public TokenResponse requestToken(String code) {
        try {
            return new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    clientSecrets.getDetails().getTokenUri(),
                    clientSecrets.getDetails().getClientId(),
                    clientSecrets.getDetails().getClientSecret(),
                    code,
                    redirectUri
            ).execute();
        } catch (Exception e) {
            log.error("Exception when requesting token", e);
            throw new IllegalStateException("Exception when requesting token: " + e.getMessage());
        }
    }

    public TokenResponse refreshToken(String refreshToken) {
        try {
            HttpExecuteInterceptor clientAuthentication = request ->
                    request.getHeaders().setAuthorization(
                            "Basic " + Base64.getEncoder().encodeToString(
                                    (
                                            clientSecrets.getDetails().getClientId()
                                                    + ":"
                                                    + clientSecrets.getDetails().getClientSecret()
                                    ).getBytes()
                            )
                    );
            return new RefreshTokenRequest(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    new GenericUrl(clientSecrets.getDetails().getTokenUri()),
                    refreshToken
            ).setClientAuthentication(clientAuthentication).execute();
        } catch (Exception ex) {
            log.error("Exception when refreshing token", ex);
            throw new IllegalStateException("Exception when refreshing token: " + ex.getMessage());
        }
    }

    public com.google.api.services.calendar.model.Calendar createCalendar(
            String token, com.google.api.services.calendar.model.Calendar calendar) {
        try {
            GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(token, null))
                    .createScoped(Collections.singleton(CalendarScopes.CALENDAR));
            Calendar service = new Calendar.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            ).setApplicationName(applicationName).build();
            return service.calendars()
                    .insert(calendar).execute();
        } catch (Exception ex) {
            log.error("Error while creating calendar", ex);
            throw new IllegalStateException("Error while creating calendar: " + ex.getMessage());
        }
    }

    public com.google.api.services.calendar.model.Event addEventToCalendar(String token, com.google.api.services.calendar.model.Event event,
                                                                           String calendarId) {
        try {
            GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(token, null))
                    .createScoped(Collections.singleton(CalendarScopes.CALENDAR));
            Calendar service = new Calendar.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            ).setApplicationName(applicationName).build();
            return service.events().insert(calendarId, event)
                    .execute();
        } catch (Exception ex) {
            log.error("Error while creating event", ex);
            throw new IllegalStateException("Error while creating event: " + ex.getMessage());
        }
    }

    public AclRule addAclRule(String token, AclRule rule, String calendarId) {
        try {
            GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(token, null))
                    .createScoped(Collections.singleton(CalendarScopes.CALENDAR));
            Calendar service = new Calendar.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            ).setApplicationName(applicationName).build();
            return service.acl().insert(calendarId, rule)
                    .execute();
        } catch (Exception ex) {
            log.error("Error while creating ACL rule", ex);
            throw new IllegalStateException("Error while creating ACL rule: " + ex.getMessage());
        }
    }
}
