package faang.school.projectservice.service.calendar;

import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.model.calendar.GoogleCalendarToken;
import faang.school.projectservice.repository.GoogleCalendarTokenRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.team.TeamMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarApiService {
    private static final String TIMEZONE = "UTC";
    private final GoogleClientSecrets clientSecrets;
    private final GoogleCalendarTokenRepository googleCalendarTokenRepository;
    private final UserContext userContext;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;
    private final UserServiceClient userServiceClient;
    @Value("${google.redirect-uri}")
    private String redirectUri;
    @Value("${google.application-name}")
    private String applicationName;

    public void acquireToken(String code) {
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
            GoogleCalendarToken token = googleCalendarTokenRepository.findByUserId(userContext.getUserId());
            if (token == null) {
                token = GoogleCalendarToken.builder().userId(userContext.getUserId())
                        .token(tokenResponse.getAccessToken())
                        .refreshToken(tokenResponse.getRefreshToken())
                        .expirationTime(LocalDateTime.now().plusSeconds(tokenResponse.getExpiresInSeconds()))
                        .build();
                googleCalendarTokenRepository.save(token);
            } else {
                log.info("Google calendar token for user with id {} already exists", userContext.getUserId());
                throw new IllegalStateException("Token for this user already exists");
            }
        } catch (Exception e) {
            log.error("Exception when saving token", e);
            throw new IllegalStateException("Exception when saving token: " + e.getMessage());
        }
    }

    public String createCalendar(String calendarSummary) {
        String token = validateGoogleAuth();
        try {
            GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(token, null))
                    .createScoped(Collections.singleton(CalendarScopes.CALENDAR));
            Calendar service = new Calendar.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            ).setApplicationName(applicationName).build();
            com.google.api.services.calendar.model.Calendar newCalendar
                    = new com.google.api.services.calendar.model.Calendar();
            newCalendar.setSummary("Project: " + calendarSummary);
            newCalendar.setTimeZone(TIMEZONE);
            com.google.api.services.calendar.model.Calendar createdCalendar = service.calendars()
                    .insert(newCalendar).execute();
            log.info("Created google calendar with id {}", createdCalendar.getId());
            return createdCalendar.getId();
        } catch (Exception ex) {
            log.error("Error while creating calendar", ex);
            throw new IllegalStateException("Error while creating calendar: " + ex.getMessage());
        }
    }

    public String addEventToCalendar(
            String calendarId,
            String summary,
            String description,
            LocalDateTime start,
            LocalDateTime end
    ) {
        String token = validateGoogleAuth();
        try {
            GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(token, null))
                    .createScoped(Collections.singleton(CalendarScopes.CALENDAR));
            Calendar service = new Calendar.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            ).setApplicationName(applicationName).build();
            com.google.api.services.calendar.model.Event event = new com.google.api.services.calendar.model.Event();
            event.setSummary(summary);
            event.setDescription(description);
            event.setStart(new EventDateTime().setDateTime(new DateTime(start.format(DateTimeFormatter.ISO_DATE_TIME)))
                    .setTimeZone(TIMEZONE));
            event.setEnd(new EventDateTime().setDateTime(new DateTime(end.format(DateTimeFormatter.ISO_DATE_TIME)))
                    .setTimeZone(TIMEZONE));
            com.google.api.services.calendar.model.Event createdEvent = service.events().insert(calendarId, event)
                    .execute();
            log.info("Event created successfully with ID: {}", createdEvent.getId());
            return createdEvent.getId();
        } catch (Exception ex) {
            log.error("Error while creating event", ex);
            throw new IllegalStateException("Error while creating event: " + ex.getMessage());
        }
    }

    public String addAclRule(String calendarId, String email, String role) {
        String token = validateGoogleAuth();
        try {
            GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(token, null))
                    .createScoped(Collections.singleton(CalendarScopes.CALENDAR));
            Calendar service = new Calendar.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            ).setApplicationName(applicationName).build();
            com.google.api.services.calendar.model.AclRule rule = new com.google.api.services.calendar.model.AclRule();
            com.google.api.services.calendar.model.AclRule.Scope scope = new com.google.api.services.calendar.model.AclRule.Scope();
            scope.setType("user");
            scope.setValue(email);
            rule.setScope(scope);
            rule.setRole(role);
            com.google.api.services.calendar.model.AclRule createdRule = service.acl().insert(calendarId, rule).execute();
            log.info("Created ACL rule with ID: {}", createdRule.getId());
            return createdRule.getId();
        } catch (Exception ex) {
            log.error("Error while creating ACL rule", ex);
            throw new IllegalStateException("Error while creating ACL rule: " + ex.getMessage());
        }
    }

    private void refreshToken(GoogleCalendarToken token) {
        try {
            HttpExecuteInterceptor clientAuthentication = new HttpExecuteInterceptor() {
                @Override
                public void intercept(HttpRequest request) throws IOException {
                    request.getHeaders().setAuthorization("Basic " + Base64.getEncoder().encodeToString(
                            (clientSecrets.getDetails().getClientId() + ":" + clientSecrets.getDetails().getClientSecret()).getBytes())
                    );
                }
            };
            TokenResponse tokenResponse = new RefreshTokenRequest(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    new GenericUrl(clientSecrets.getDetails().getTokenUri()),
                    token.getRefreshToken()
            ).setClientAuthentication(clientAuthentication).execute();
            token.setToken(tokenResponse.getAccessToken());
            if (tokenResponse.getRefreshToken() != null) {
                token.setRefreshToken(tokenResponse.getRefreshToken());
            }
            token.setExpirationTime(LocalDateTime.now().plusSeconds(tokenResponse.getExpiresInSeconds()));
            googleCalendarTokenRepository.save(token);
            log.info("Refreshed token {}", token);
        } catch (Exception ex) {
            log.error("Exception when refreshing token for user with id {}", token.getUserId(), ex);
            throw new IllegalStateException("Exception when refreshing token: " + ex.getMessage());
        }
    }

    private String validateGoogleAuth() {
        long userId = userContext.getUserId();
        GoogleCalendarToken googleCalendarToken = googleCalendarTokenRepository.findByUserId(userId);
        if (googleCalendarToken == null) {
            log.error("User with id {} tried to create google calendar but no google token found", userId);
            throw new AccessDeniedException("Authorization from google needed to create a calendar");
        }
        if (googleCalendarToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            refreshToken(googleCalendarToken);
        }
        return googleCalendarToken.getToken();
    }
}
