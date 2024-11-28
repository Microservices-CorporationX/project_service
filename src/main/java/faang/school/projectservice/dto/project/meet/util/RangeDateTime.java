package faang.school.projectservice.dto.project.meet.util;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import faang.school.projectservice.deserialization.UnixTimestampToLocalDateTimeDeserializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RangeDateTime {

    @JsonDeserialize(using = UnixTimestampToLocalDateTimeDeserializer.class)
    private LocalDateTime start;

    @JsonDeserialize(using = UnixTimestampToLocalDateTimeDeserializer.class)
    private LocalDateTime end;
}
