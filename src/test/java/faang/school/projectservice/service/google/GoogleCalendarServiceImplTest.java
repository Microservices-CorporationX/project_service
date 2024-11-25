package faang.school.projectservice.service.google;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.google.GoogleCalendarConfig;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.event.EventDto;
import faang.school.projectservice.repository.GoogleTokenRepository;
import faang.school.projectservice.validator.google.GoogleCalendarValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleCalendarServiceImplTest {
    private static final long EVENT_ID = 1L;
    private static final long USER_ID = 2L;
    private static final LocalDateTime START_TIME = LocalDateTime.of(2024, 1, 2, 3, 4);
    private static final LocalDateTime END_TIME = LocalDateTime.of(2024, 1, 2, 4, 5);

    @Mock
    private GoogleTokenRepository googleTokenRepository;
    @Mock
    private GoogleCalendarConfig googleCalendarConfig;
    @Mock
    private GoogleCalendarValidator googleCalendarValidator;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private GoogleCalendarServiceImpl googleCalendarService;

    private EventDto eventDto;
    private UserDto userDto;


    @BeforeEach
    void setUp() {
        eventDto = EventDto.builder()
                .id(EVENT_ID)
                .title("title")
                .description("description")
                .startDate(START_TIME)
                .endDate(END_TIME)
                .location("location")
                .build();

        userDto = UserDto.builder()
                .id(USER_ID)
                .participatedEventIds(List.of(EVENT_ID))
                .build();
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when user or event validation fails")
    void createEvent_WhenValidationFails_ThrowsEntityNotFoundException() {
        when(googleCalendarValidator.checkUserAndEvent(anyLong(), anyLong())).thenThrow(EntityNotFoundException.class);
        Assert.assertThrows(EntityNotFoundException.class,
                () -> googleCalendarService.createEvent(USER_ID, EVENT_ID));
    }


    @Test
    @DisplayName("Should return authorization link when user needs to authorize")
    void createEvent_WhenUserNeedsAuthorization_ReturnsAuthLink() throws IOException {
        String link = String.format("http://example%d-%d.com", USER_ID, EVENT_ID);
        String expected = String.format("follow the link to authorize in calendar: %s", link);
        when(googleCalendarValidator.checkUserAndEvent(anyLong(), anyLong())).thenReturn(true);
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(userServiceClient.getEventById(EVENT_ID)).thenReturn(eventDto);
        when(googleTokenRepository.existsGoogleTokenByUserId(USER_ID)).thenReturn(false);
        when(googleCalendarConfig.getAuthorizationUrl(USER_ID, EVENT_ID)).thenReturn(link);
        String actual = googleCalendarService.createEvent(USER_ID, EVENT_ID);
        assertThat(actual).isEqualTo(expected);
    }
}