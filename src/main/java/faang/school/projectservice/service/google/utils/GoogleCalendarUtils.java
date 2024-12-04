package faang.school.projectservice.service.google.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@Slf4j
public class GoogleCalendarUtils {

    public static NetHttpTransport createHttpTransport() {
        try {
            return GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            log.error("Error creating HTTP transport", e);
            throw new RuntimeException("Failed to create HTTP transport", e);
        }
    }

    public Calendar getService(Credential credential) {
        return new Calendar.Builder(createHttpTransport(), credential.getJsonFactory(), credential)
                .build();
    }

    public EventDateTime convertLocalDateTimeToEventDateTime(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        DateTime dateTime = new DateTime(zonedDateTime.toInstant().toEpochMilli());
        return new EventDateTime()
                .setDateTime(dateTime);
    }
}