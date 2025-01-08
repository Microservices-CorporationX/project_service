package faang.school.projectservice.service.calendar;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.client.calendar.GoogleCalendarClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.model.calendar.GoogleCalendarToken;
import faang.school.projectservice.repository.GoogleCalendarTokenRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private final GoogleCalendarClient googleCalendarClient;
    @Value("${google.redirect-uri}")
    private String redirectUri;
    @Value("${google.application-name}")
    private String applicationName;

    public String getAuthUrl() {
        log.info(redirectUri);
        return clientSecrets.getDetails().getAuthUri()
                + "?client_id=" + clientSecrets.getDetails().getClientId()
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&scope=" + URLEncoder.encode("https://www.googleapis.com/auth/calendar https://www.googleapis.com/auth/calendar.events", StandardCharsets.UTF_8)
                + "&response_type=code"
                + "&access_type=offline"
                + "&approval_prompt=force";
    }

    public void acquireToken(String code) {
        TokenResponse tokenResponse = googleCalendarClient.requestToken(code);
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
            throw new IllegalStateException("Google calendar token for this user already exists");
        }
    }

    public String createCalendar(String calendarSummary) {
        String token = validateGoogleAuth();
        com.google.api.services.calendar.model.Calendar newCalendar
                = new com.google.api.services.calendar.model.Calendar();
        newCalendar.setSummary("Project: " + calendarSummary);
        newCalendar.setTimeZone(TIMEZONE);
        com.google.api.services.calendar.model.Calendar createdCalendar = googleCalendarClient
                .createCalendar(token, newCalendar);
        log.info("Created google calendar with id {}for user with id: {}", createdCalendar.getId(), userContext.getUserId());
        return createdCalendar.getId();
    }

    public String addEventToCalendar(
            String calendarId,
            String summary,
            String description,
            LocalDateTime start,
            LocalDateTime end
    ) {
        String token = validateGoogleAuth();
        com.google.api.services.calendar.model.Event event = new com.google.api.services.calendar.model.Event();
        event.setSummary(summary);
        event.setDescription(description);
        event.setStart(new EventDateTime().setDateTime(new DateTime(start.format(DateTimeFormatter.ISO_DATE_TIME)))
                .setTimeZone(TIMEZONE));
        event.setEnd(new EventDateTime().setDateTime(new DateTime(end.format(DateTimeFormatter.ISO_DATE_TIME)))
                .setTimeZone(TIMEZONE));
        com.google.api.services.calendar.model.Event createdEvent = googleCalendarClient
                .addEventToCalendar(token, event, calendarId);
        log.info("Event created successfully with id: {} for user with id: {}", createdEvent.getId(), userContext.getUserId());
        return createdEvent.getId();
    }

    public String addAclRule(String calendarId, String email, String role) {
        String token = validateGoogleAuth();
        com.google.api.services.calendar.model.AclRule rule = new com.google.api.services.calendar.model.AclRule();
        com.google.api.services.calendar.model.AclRule.Scope scope = new com.google.api.services.calendar.model.AclRule.Scope();
        scope.setType("user");
        scope.setValue(email);
        rule.setScope(scope);
        rule.setRole(role);
        com.google.api.services.calendar.model.AclRule createdRule = googleCalendarClient
                .addAclRule(token, rule, calendarId);
        log.info("Created ACL rule with id: {} for user with id {}", createdRule.getId(), userContext.getUserId());
        return createdRule.getId();
    }

    private void refreshToken(GoogleCalendarToken token) {
        TokenResponse tokenResponse = googleCalendarClient.refreshToken(token.getRefreshToken());
        token.setToken(tokenResponse.getAccessToken());
        if (tokenResponse.getRefreshToken() != null) {
            token.setRefreshToken(tokenResponse.getRefreshToken());
        }
        token.setExpirationTime(LocalDateTime.now().plusSeconds(tokenResponse.getExpiresInSeconds()));
        googleCalendarTokenRepository.save(token);
        log.info("Refreshed token for google calendar {} for user with id {}", token, userContext.getUserId());
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
