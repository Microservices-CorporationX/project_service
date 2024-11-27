package faang.school.projectservice.dto.project.meet;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class MeetFilterDto {
    private String title;
    private Map.Entry<LocalDateTime, LocalDateTime> rangeDateTime;
}
