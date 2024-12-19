package faang.school.projectservice.service.google;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.client.google.calendar.GoogleCalendarClient;
import faang.school.projectservice.dto.project.meet.MeetDto;
import faang.school.projectservice.dto.project.meet.MeetFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.project.meet.MeetService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {
    private final GoogleCalendarClient googleCalendarClient;
    private final UserServiceClient userServiceClient;
    private final ProjectService projectService;
    private final MeetService meetService;

    public MeetDto getEvent(long meetId) {
        return meetService.getMeetByIdAndUserId(meetId);
    }

    @Transactional
    public List<MeetDto> getEvents(long projectId, MeetFilterDto meetFilterDto) throws IOException {
        List<MeetDto> meetDtos = meetService.getMeets(projectId, meetFilterDto);

        Calendar calendarService = googleCalendarClient.getCalendarService();
        Project project = projectService.getProjectEntityById(projectId);

        Events events = calendarService.events().list(googleCalendarClient.findCalendarBySummary(project, calendarService).getId())
                .setTimeMin(new DateTime(System.currentTimeMillis()))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        List<String> eventIds = events.getItems().stream()
                .map(Event::getId)
                .toList();

        return meetDtos.stream()
                .filter(meetDto -> eventIds.contains(meetDto.getGoogleEventId()))
                .toList();
    }

    @Transactional
    public MeetDto createEvent(MeetDto meetDto) throws IOException {
        meetDto = meetService.createMeet(meetDto);

        Calendar calendarService = googleCalendarClient.getCalendarService();
        Event event = mappingMeetToEvent(meetDto);

        Project project = projectService.getProjectEntityById(meetDto.getProjectId());
        com.google.api.services.calendar.model.Calendar calendar = googleCalendarClient.findCalendarBySummary(project, calendarService);

        event = calendarService.events().insert(calendar.getId(), event).execute();

        meetDto.setGoogleCalendarEventLink(event.getHtmlLink());
        meetDto.setGoogleEventId(event.getId());
        return meetService.updateMeet(meetDto);
    }

    @Transactional
    public MeetDto updateEvent(MeetDto meetDto) throws IOException {
        if (meetDto.getGoogleEventId() == null) {
            throw new IllegalStateException("This meeting is not on the google calendar");
        }
        meetDto = meetService.updateMeet(meetDto);

        Calendar calendarService = googleCalendarClient.getCalendarService();
        Event event = mappingMeetToEvent(meetDto);

        Project project = projectService.getProjectEntityById(meetDto.getProjectId());
        com.google.api.services.calendar.model.Calendar calendar = googleCalendarClient.findCalendarBySummary(project, calendarService);

        calendarService.events().update(calendar.getId(), meetDto.getGoogleEventId(), event).execute();
        return meetDto;
    }

    private Event mappingMeetToEvent(MeetDto meetDto) {
        Event.Creator creator = new Event.Creator();
        creator.setEmail(userServiceClient.getGoogleEmailOrDefaultByUserId(meetDto.getCreatorId()));

        List<EventAttendee> eventAttendees = userServiceClient.getGoogleEmailsOrDefaultByUserIds(meetDto.getUserIds()).stream()
                .map(email -> new EventAttendee().setEmail(email))
                .toList();

        return new Event()
                .setSummary(meetDto.getTitle())
                .setDescription(meetDto.getDescription())
                .setStart(localDateTime2EventDateTime(meetDto.getStartDateTime()))
                .setEnd(localDateTime2EventDateTime(meetDto.getEndDateTime()))
                .setCreator(creator)
                .setAttendees(eventAttendees);
    }

    private EventDateTime localDateTime2EventDateTime(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);

        return new EventDateTime().setDateTime(new DateTime(zonedDateTime.toInstant().toEpochMilli()));
    }
}
