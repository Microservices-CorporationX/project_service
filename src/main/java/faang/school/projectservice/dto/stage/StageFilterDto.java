package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StageFilterDto {
    private TeamRole teamRole;
    private TaskStatus taskStatus;
}
