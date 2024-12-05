package faang.school.projectservice.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class EpochSecondToLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        try {
            long timestamp = Long.parseLong(p.getText());
            Instant instant = Instant.ofEpochSecond(timestamp);

            return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (NumberFormatException e) {
            int[] dateArray = p.readValueAs(int[].class);

            if (dateArray.length == 6) {
                return LocalDateTime.of(
                        dateArray[0],
                        dateArray[1],
                        dateArray[2],
                        dateArray[3],
                        dateArray[4],
                        dateArray[5],
                        0
                );
            }

            if (dateArray.length == 7) {
                return LocalDateTime.of(
                        dateArray[0],
                        dateArray[1],
                        dateArray[2],
                        dateArray[3],
                        dateArray[4],
                        dateArray[5],
                        dateArray[6]
                );
            }

            throw new IOException("Invalid date array format");
        }
    }
}
