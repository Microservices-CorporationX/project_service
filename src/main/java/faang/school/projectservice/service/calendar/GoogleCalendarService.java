package faang.school.projectservice.service.calendar;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.event.EventDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.calendar.GoogleCalendarToken;
import faang.school.projectservice.repository.GoogleCalendarTokenRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.team.TeamMemberService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Collections;


@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarService {
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
            GoogleCalendarToken token = googleCalendarTokenRepository.findByUserId(userContext.getUserId());
            if (token == null) {
                token = GoogleCalendarToken.builder().userId(userContext.getUserId())
                        .token(tokenResponse.getAccessToken())
                        .build();
            }
            googleCalendarTokenRepository.save(token);
        } catch (Exception e) {
            log.error("Exception when saving token", e);
            throw new IllegalStateException("Exception when saving token: " + e.getMessage());
        }
    }

    public void createProjectCalendar(long projectId) {
        long userId = userContext.getUserId();
        TeamMember teamMember = teamMemberService.validateUserIsProjectMember(userId, projectId);
        if (teamMember.getRoles() == null || !teamMember.getRoles().contains(TeamRole.OWNER)) {
            log.info("User with id {} tried to create google calendar for project with id {} but is not the owner",
                    userId, projectId);
            throw new AccessDeniedException("Only project owner can create google calendar for project");
        }
        Project project = teamMember.getTeam().getProject();
        GoogleCalendarToken googleCalendarToken = googleCalendarTokenRepository.findByUserId(userId);
        if (googleCalendarToken == null) {
            log.error("User with id {} tried to create google calendar for project with id {} but no google token found",
                    userId, projectId);
            throw new AccessDeniedException("Can not work with google calendar without token from google");
        }
        String token = googleCalendarToken.getToken();
        try {
            GoogleCredentials credentials = getCredentials(token);
            Calendar service = getService(credentials);
            com.google.api.services.calendar.model.Calendar newCalendar
                    = new com.google.api.services.calendar.model.Calendar();
            newCalendar.setSummary("Project: " + project.getName());
            newCalendar.setTimeZone(TIMEZONE);
            com.google.api.services.calendar.model.Calendar createdCalendar = service.calendars().insert(newCalendar).execute();
            String calendarId = createdCalendar.getId();
            project.setCalendarId(calendarId);
            projectService.save(project);
            log.info("Calendar created successfully with ID: {}", calendarId);
        } catch (Exception ex) {
            log.error("Error while creating calendar", ex);
            throw new IllegalStateException("Error while creating calendar: " + ex.getMessage());
        }
    }

    public void addEvent(long projectId, long eventId) {
        EventDto eventDto;
        try {
            eventDto = userServiceClient.getEvent(eventId);
        } catch (FeignException.NotFound e) {
            log.info("Event with id {} not found", eventId);
            throw new EntityNotFoundException("Event not found");
        }
        long userId = userContext.getUserId();
        TeamMember teamMember = teamMemberService.validateUserIsProjectMember(userId, projectId);
        if (teamMember.getRoles() == null || !teamMember.getRoles().contains(TeamRole.MANAGER)
                || !teamMember.getRoles().contains(TeamRole.OWNER)) {
            log.info("User with id {} tried to create add event to google calendar for project with id {} " +
                            "but is not the owner or manager",
                    userId, projectId);
            throw new AccessDeniedException("Must be owner or manager to add event to google calendar");
        }
        GoogleCalendarToken googleCalendarToken = googleCalendarTokenRepository.findByUserId(userId);
        if (googleCalendarToken == null) {
            throw new AccessDeniedException("Cannot work with Google Calendar without a token.");
        }
        Project project = teamMember.getTeam().getProject();
        if (project.getCalendarId() == null) {
            throw new IllegalStateException("No calendar associated with the project.");
        }
        String token = googleCalendarToken.getToken();
        try {
            GoogleCredentials credentials = getCredentials(token);
            Calendar service = getService(credentials);
            com.google.api.services.calendar.model.Event event = new com.google.api.services.calendar.model.Event();
            event.setSummary(eventDto.getTitle());
            event.setDescription(eventDto.getDescription());
            event.setStart(new EventDateTime().setDateTime(new DateTime(eventDto.getStartDate().format(DateTimeFormatter.ISO_DATE_TIME)))
                    .setTimeZone(TIMEZONE));
            event.setEnd(new EventDateTime().setDateTime(new DateTime(eventDto.getEndDate().format(DateTimeFormatter.ISO_DATE_TIME)))
                    .setTimeZone(TIMEZONE));
            com.google.api.services.calendar.model.Event createdEvent = service.events().insert(project.getCalendarId(), event).execute();
            log.info("Event created successfully with ID: {}", createdEvent.getId());
        } catch (Exception ex) {
            log.error("Error while creating event", ex);
            throw new IllegalStateException("Error while creating event: " + ex.getMessage());
        }
    }

    private GoogleCredentials getCredentials(String token) {
        return GoogleCredentials.create(new AccessToken(token, null))
                .createScoped(Collections.singleton(CalendarScopes.CALENDAR));
    }

    private Calendar getService(GoogleCredentials credentials) {
        return new Calendar.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
        ).setApplicationName(applicationName).build();
    }
}
