package faang.school.projectservice.dto.project.meet;

import faang.school.projectservice.dto.project.meet.util.RangeDateTime;
import lombok.Data;

@Data
public class MeetFilterDto {
    private String title;
    private RangeDateTime rangeDateTime;
}
