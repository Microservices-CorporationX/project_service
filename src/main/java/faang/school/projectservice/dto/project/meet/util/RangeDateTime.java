package faang.school.projectservice.dto.project.meet.util;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import faang.school.projectservice.deserialization.EpochSecondToLocalDateTimeDeserializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RangeDateTime {

    @JsonDeserialize(using = EpochSecondToLocalDateTimeDeserializer.class)
    private LocalDateTime start;

    @JsonDeserialize(using = EpochSecondToLocalDateTimeDeserializer.class)
    private LocalDateTime end;
}
