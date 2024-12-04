package faang.school.projectservice.service.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.client.UserServiceClient;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.config.google.GoogleProperties;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.event.EventDto;
import faang.school.projectservice.repository.GoogleTokenRepository;
import faang.school.projectservice.service.google.auth.GoogleCalendarAuth;
import faang.school.projectservice.service.google.utils.GoogleCalendarUtils;
import faang.school.projectservice.validator.google.GoogleCalendarValidator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

    private final GoogleProperties googleProperties;
    private final GoogleCalendarValidator googleCalendarValidator;
    private final UserServiceClient userServiceClient;
    private final GoogleTokenRepository googleTokenRepository;
    private final GoogleCalendarUtils utils;
    private final GoogleCalendarAuth auth;


    @Override
    public String authorizeUser(String authorizationCode, Long userId) throws IOException {
        GoogleClientSecrets googleClientSecrets = auth.getClientSecrets();
        GoogleAuthorizationCodeFlow codeFlow = auth.getCodeFlow(googleClientSecrets);
        GoogleTokenResponse tokenResponse = codeFlow.newTokenRequest(authorizationCode)
                .setRedirectUri(googleProperties.getRedirectUrl())
                .execute();
        codeFlow.createAndStoreCredential(tokenResponse, String.valueOf(userId));
        return authorizationCode;
    }

    @Override
    public String createEvent(Long userId, Long eventId) throws IOException {
        googleCalendarValidator.checkUserAndEvent(userId, eventId);
        UserDto userDto = userServiceClient.getUser(userId);
        EventDto eventDto = userServiceClient.getEventById(eventId);
        if (!googleTokenRepository.existsGoogleTokenByUserId(userId)) {
            return String.format("follow the link to authorize in calendar: %s", auth.getAuthorizationUrl(userId, eventId));
        }
        String calendarId = googleProperties.getCalendarId();
        Event event = createEvent(eventDto);
        Calendar calendar = createService(userDto);
        Event result = calendar.events().insert(calendarId, event).execute();
        return String.format("follow the link to enter your event in calendar: %s", result.getHtmlLink());
    }

    private Event createEvent(EventDto eventDto) {
        Event event = new Event();
        event.setSummary(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        EventDateTime startDateTime = utils.convertLocalDateTimeToEventDateTime(eventDto.getStartDate());
        EventDateTime endDateTime = utils.convertLocalDateTimeToEventDateTime(eventDto.getEndDate());
        event.setStart(startDateTime);
        event.setEnd(endDateTime);
        return event;
    }

    public Calendar createService(UserDto userDto) throws IOException {
        GoogleClientSecrets clientSecrets = auth.getClientSecrets();
        GoogleAuthorizationCodeFlow codeFlow = auth.getCodeFlow(clientSecrets);
        Credential credential = codeFlow.loadCredential(String.valueOf(userDto.getId()));
        return utils.getService(credential);
    }
}