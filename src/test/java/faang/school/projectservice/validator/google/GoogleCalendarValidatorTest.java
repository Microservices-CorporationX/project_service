package faang.school.projectservice.validator.google;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.event.EventDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoogleCalendarValidatorTest {
    private static final long USER_ID = 1L;
    private static final long EVENT_ID = 2L;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private GoogleCalendarValidator googleCalendarValidator;
    private UserDto userDto;
    private EventDto eventDto;
    private List<Long> participatedEventIds;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(USER_ID)
                .participatedEventIds(new ArrayList<>())
                .build();

        eventDto = EventDto.builder()
                .id(EVENT_ID)
                .build();

        participatedEventIds = userDto.getParticipatedEventIds();
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when user or event not found")
    void checkUserAndEvent_WhenUserOrEventNotFound_ThrowsEntityNotFoundException() {
        Assert.assertThrows(EntityNotFoundException.class,
                () -> googleCalendarValidator.checkUserAndEvent(USER_ID, EVENT_ID));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when user is not participant of event")
    void checkUserAndEvent_WhenUserIsNotParticipant_ThrowsIllegalArgumentException() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(userServiceClient.getEventById(EVENT_ID)).thenReturn(eventDto);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> googleCalendarValidator.checkUserAndEvent(USER_ID, EVENT_ID));
    }

    @Test
    @DisplayName("Should return true when user is participant of event")
    void checkUserAndEvent_WhenUserIsParticipant_ReturnsTrue() {
        participatedEventIds.add(EVENT_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(userServiceClient.getEventById(EVENT_ID)).thenReturn(eventDto);
        assertThat(googleCalendarValidator.checkUserAndEvent(USER_ID, EVENT_ID)).isTrue();
    }
}