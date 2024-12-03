package faang.school.projectservice.service.calendar;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.client.calendar.GoogleCalendarClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.model.calendar.GoogleCalendarToken;
import faang.school.projectservice.repository.GoogleCalendarTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleCalendarApiServiceTest {
    @Mock
    private GoogleCalendarTokenRepository googleCalendarTokenRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private GoogleCalendarClient googleCalendarClient;
    @InjectMocks
    private GoogleCalendarApiService googleCalendarApiService;
    private GoogleCalendarToken googleCalendarToken;

    @BeforeEach
    void setUp() {
        googleCalendarToken = GoogleCalendarToken.builder()
                .userId(1L)
                .token("access-token")
                .refreshToken("refresh-token")
                .expirationTime(LocalDateTime.now().plusMinutes(10))
                .build();
    }

    @Test
    void testAcquireTokenWithTokenNotExists() {
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken("new-access-token");
        tokenResponse.setRefreshToken("new-refresh-token");
        tokenResponse.setExpiresInSeconds(3600L);
        when(userContext.getUserId()).thenReturn(1L);
        when(googleCalendarTokenRepository.findByUserId(1L)).thenReturn(null);
        when(googleCalendarClient.requestToken("code")).thenReturn(tokenResponse);
        googleCalendarApiService.acquireToken("code");
        verify(googleCalendarTokenRepository, times(1)).save(any(GoogleCalendarToken.class));
    }

    @Test
    void testAcquireTokenWithTokenExists() {
        when(userContext.getUserId()).thenReturn(1L);
        when(googleCalendarTokenRepository.findByUserId(1L)).thenReturn(googleCalendarToken);
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> googleCalendarApiService.acquireToken("code"));
        assertEquals("Token for this user already exists", exception.getMessage());
    }

    @Test
    void testCreateCalendarCreated() {
        when(userContext.getUserId()).thenReturn(1L);
        when(googleCalendarTokenRepository.findByUserId(1L)).thenReturn(googleCalendarToken);
        Calendar calendar = new Calendar();
        calendar.setId("calendar-id");
        when(googleCalendarClient.createCalendar(anyString(), any(Calendar.class))).thenReturn(calendar);
        String calendarId = googleCalendarApiService.createCalendar("Test Calendar");
        assertEquals("calendar-id", calendarId);
        verify(googleCalendarClient, times(1)).createCalendar(eq("access-token"), any(Calendar.class));
    }

    @Test
    void testAddEventToCalendarAdded() {
        when(userContext.getUserId()).thenReturn(1L);
        when(googleCalendarTokenRepository.findByUserId(1L)).thenReturn(googleCalendarToken);
        Event event = new Event();
        event.setId("event-id");
        when(googleCalendarClient.addEventToCalendar(anyString(), any(Event.class), eq("calendar-id"))).thenReturn(event);
        String eventId = googleCalendarApiService.addEventToCalendar(
                "calendar-id",
                "Test Event",
                "Test Description",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1)
        );
        assertEquals("event-id", eventId);
        verify(googleCalendarClient, times(1))
                .addEventToCalendar(eq("access-token"), any(Event.class), eq("calendar-id"));
    }

    @Test
    void testAddAclRuleAdded() {
        when(userContext.getUserId()).thenReturn(1L);
        when(googleCalendarTokenRepository.findByUserId(1L)).thenReturn(googleCalendarToken);
        AclRule rule = new AclRule();
        rule.setId("acl-id");
        when(googleCalendarClient.addAclRule(anyString(), any(AclRule.class), eq("calendar-id"))).thenReturn(rule);
        String aclId = googleCalendarApiService.addAclRule("calendar-id", "test@example.com", "owner");
        assertEquals("acl-id", aclId);
        verify(googleCalendarClient, times(1)).addAclRule(eq("access-token"), any(AclRule.class), eq("calendar-id"));
    }

    @Test
    void testValidateGoogleAuthWithTokenNotFound() {
        when(userContext.getUserId()).thenReturn(1L);
        when(googleCalendarTokenRepository.findByUserId(1L)).thenReturn(null);
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> googleCalendarApiService.createCalendar("Test Calendar"));
        assertEquals("Authorization from google needed to create a calendar", exception.getMessage());
    }

    @Test
    void testValidateGoogleAuthWithTokenExpired() {
        googleCalendarToken.setExpirationTime(LocalDateTime.now().minusMinutes(1));
        when(userContext.getUserId()).thenReturn(1L);
        when(googleCalendarTokenRepository.findByUserId(1L)).thenReturn(googleCalendarToken);
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken("new-access-token");
        tokenResponse.setRefreshToken("new-refresh-token");
        tokenResponse.setExpiresInSeconds(3600L);
        when(googleCalendarClient.refreshToken("refresh-token")).thenReturn(tokenResponse);
        when(googleCalendarClient.createCalendar(any(), any())).thenReturn(new Calendar().setId("id"));
        googleCalendarApiService.createCalendar("Test Calendar");
        verify(googleCalendarClient, times(1)).refreshToken("refresh-token");
        verify(googleCalendarTokenRepository, times(1)).save(any(GoogleCalendarToken.class));
    }
}