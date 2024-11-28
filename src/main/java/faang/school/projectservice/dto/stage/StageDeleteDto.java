package faang.school.projectservice.dto.stage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StageDeleteDto {
    private Long stageId;
    private ActionWithTaskDto actionWithTaskDto;
}
