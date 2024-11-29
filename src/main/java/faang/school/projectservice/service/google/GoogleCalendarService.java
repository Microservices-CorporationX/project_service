package faang.school.projectservice.service.google;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import faang.school.projectservice.config.security.GoogleClintSecretConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GoogleCalendarService {

    private Calendar service;

    GoogleCalendarService(GoogleClintSecretConfig secretConfig) throws IOException {
     service = new Calendar.Builder(new NetHttpTransport(), new GsonFactory(), secretConfig.googleClientSecrets().)
                .setApplicationName("app name")
                .build();
    }

    public void listEvents() throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary") // primary - это основной календарь пользователя
                .setTimeMin(now)
                .setMaxResults(10)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        System.out.println("Upcoming events:");
        for (Event event : events.getItems()) {
            DateTime start = event.getStart().getDateTime();
            if (start == null) {
                start = event.getStart().getDate();
            }
            System.out.printf("%s (%s)\n", event.getSummary(), start);
        }
    }

    public void createEvent() throws IOException {
        Event event = new Event()
                .setSummary("Google I/O 2024")
                .setLocation("800 Howard St., San Francisco, CA 94103")
                .setDescription("A chance to hear more about Google's developer products.")
                .setStart(new EventDateTime().setDateTime(new DateTime("2024-05-28T09:00:00-07:00")).setTimeZone("America/Los_Angeles"))
                .setEnd(new EventDateTime().setDateTime(new DateTime("2024-05-28T17:00:00-07:00")).setTimeZone("America/Los_Angeles"));

        event = service.events().insert("primary", event).execute();

        System.out.println("Event created: " + event.getHtmlLink());
    }

}
