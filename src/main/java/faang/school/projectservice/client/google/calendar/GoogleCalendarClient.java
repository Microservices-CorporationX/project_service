package faang.school.projectservice.client.google.calendar;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.model.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class GoogleCalendarClient {
    private final UserServiceClient userServiceClient;

    public com.google.api.services.calendar.model.Calendar findCalendarBySummary(Project project, Calendar calendarService) throws IOException {
        CalendarList calendarList = calendarService.calendarList().list().execute();

        for (CalendarListEntry entry : calendarList.getItems()) {
            if (entry.getSummary().equals(project.getName())) {
                return calendarService.calendars().get(entry.getId()).execute();
            }
        }

        return createCalendar(project.getName(), project.getDescription(), calendarService);
    }

    public com.google.api.services.calendar.model.Calendar createCalendar(String summary, String description, Calendar calendarService) throws IOException {
        com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
        calendar.setSummary(summary);
        calendar.setTimeZone(ZoneId.systemDefault().toString());
        calendar.setDescription(description);

        return calendarService.calendars().insert(calendar).execute();
    }

    public Calendar getCalendarService() {
        return new Calendar.Builder(
                new NetHttpTransport(),
                new GsonFactory(),
                new GoogleCredential().setAccessToken(userServiceClient.getAccessToken())
        ).setApplicationName("Google Calendar API").build();
    }
}
