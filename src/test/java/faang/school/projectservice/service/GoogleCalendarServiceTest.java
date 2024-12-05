package faang.school.projectservice.service;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.client.google.calendar.GoogleCalendarClient;
import faang.school.projectservice.dto.project.meet.MeetDto;
import faang.school.projectservice.dto.project.meet.MeetFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.google.GoogleCalendarService;
import faang.school.projectservice.service.project.meet.MeetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoogleCalendarServiceTest {

    @Mock
    private GoogleCalendarClient googleCalendarClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectService projectService;

    @Mock
    private MeetService meetService;

    @InjectMocks
    private GoogleCalendarService googleCalendarService;

    @Mock
    private Calendar calendarService;

    @Mock
    private Calendar.Events.List eventsList;

    @Mock
    private Calendar.Events calendarEvents;

    @Mock
    private Calendar.Events.Insert eventInsert;

    @Mock
    private Calendar.Events.Update eventUpdate;

    MeetDto meetDto;
    Project project;
    com.google.api.services.calendar.model.Calendar calendar;

    String creatorEmail;
    List<String> emails;

    @BeforeEach
    public void setUp() {
        meetDto = prepareMeetDto();

        project = new Project();
        project.setId(1L);

        calendar = new com.google.api.services.calendar.model.Calendar();
        calendar.setId("calendarId");

        String firstEmail = "first@icloud.com";
        String secondEmail = "second@icloud.com";

        creatorEmail = "email@icloud.com";
        emails = List.of(firstEmail, secondEmail);
    }

    @Test
    public void testGetEvent() {
        MeetDto meetDto = new MeetDto();
        meetDto.setId(1L);

        when(meetService.getMeetByIdAndUserId(meetDto.getId())).thenReturn(meetDto);
        MeetDto result = googleCalendarService.getEvent(meetDto.getId());

        assertEquals(meetDto, result);
    }

    @Test
    public void testGetEvents() throws IOException {
        Project project = new Project();
        project.setId(1L);
        MeetFilterDto meetFilterDto = new MeetFilterDto();

        MeetDto firstMeetDto = new MeetDto();
        firstMeetDto.setGoogleEventId("eventId");

        MeetDto secondEventDto = new MeetDto();
        secondEventDto.setGoogleEventId("id");

        List<MeetDto> meetDtos = List.of(firstMeetDto, secondEventDto);

        Event firstEvent = new Event();
        firstEvent.setId("eventId");

        Event secondEvent = new Event();
        secondEvent.setId("secondEventId");

        Events events = new Events();
        events.setItems(List.of(firstEvent, secondEvent));

        com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
        calendar.setId("calendarId");

        when(meetService.getMeets(project.getId(), meetFilterDto)).thenReturn(meetDtos);
        when(googleCalendarClient.getCalendarService()).thenReturn(calendarService);
        when(projectService.getProjectEntityById(project.getId())).thenReturn(project);

        when(googleCalendarClient.findCalendarBySummary(project, calendarService)).thenReturn(calendar);

        when(calendarService.events()).thenReturn(calendarEvents);
        when(calendarEvents.list(calendar.getId())).thenReturn(eventsList);

        when(eventsList.setTimeMin(any())).thenReturn(eventsList);
        when(eventsList.setOrderBy(any())).thenReturn(eventsList);
        when(eventsList.setSingleEvents(any())).thenReturn(eventsList);
        when(eventsList.execute()).thenReturn(events);

        List<MeetDto> result = googleCalendarService.getEvents(project.getId(), meetFilterDto);

        assertEquals(1, result.size());
        assertEquals("eventId", result.get(0).getGoogleEventId());
    }

    @Test
    public void testCreateEvent() throws IOException {
        when(meetService.createMeet(meetDto)).thenReturn(meetDto);
        when(googleCalendarClient.getCalendarService()).thenReturn(calendarService);

        when(userServiceClient.getGoogleEmailOrDefaultByUserId(meetDto.getCreatorId())).thenReturn(creatorEmail);
        when(userServiceClient.getGoogleEmailsOrDefaultByUserIds(meetDto.getUserIds()))
                .thenReturn(emails);

        when(projectService.getProjectEntityById(meetDto.getProjectId())).thenReturn(project);
        when(googleCalendarClient.findCalendarBySummary(project, calendarService)).thenReturn(calendar);

        when(calendarService.events()).thenReturn(calendarEvents);
        when(calendarEvents.insert(any(), any())).thenReturn(eventInsert);
        when(eventInsert.execute()).thenReturn(new Event());
        when(meetService.updateMeet(meetDto)).thenReturn(meetDto);

        googleCalendarService.createEvent(meetDto);
    }

    @Test
    public void testUpdateEventGoogleEventIdIsNull() {
        assertThrows(IllegalStateException.class,
                () -> googleCalendarService.updateEvent(new MeetDto()));
    }

    @Test
    public void testUpdateEvent() throws IOException {
        meetDto.setGoogleEventId("id");
        when(meetService.updateMeet(meetDto)).thenReturn(meetDto);
        when(googleCalendarClient.getCalendarService()).thenReturn(calendarService);

        when(userServiceClient.getGoogleEmailOrDefaultByUserId(meetDto.getCreatorId())).thenReturn(creatorEmail);
        when(userServiceClient.getGoogleEmailsOrDefaultByUserIds(meetDto.getUserIds()))
                .thenReturn(emails);

        when(projectService.getProjectEntityById(project.getId())).thenReturn(project);
        when(googleCalendarClient.findCalendarBySummary(project, calendarService)).thenReturn(calendar);
        when(calendarService.events()).thenReturn(calendarEvents);
        when(calendarEvents.update(any(), any(), any())).thenReturn(eventUpdate);
        when(eventUpdate.execute()).thenReturn(new Event());

        googleCalendarService.updateEvent(meetDto);
    }

    private MeetDto prepareMeetDto() {
        MeetDto meetDto = new MeetDto();
        meetDto.setId(1L);
        meetDto.setTitle("title");
        meetDto.setDescription("description");
        meetDto.setStartDateTime(LocalDateTime.now().plusHours(1));
        meetDto.setEndDateTime(LocalDateTime.now().plusHours(2));
        meetDto.setCreatorId(3L);
        meetDto.setUserIds(new ArrayList<>());
        meetDto.setProjectId(1L);

        return meetDto;
    }
}
