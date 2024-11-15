package faang.school.projectservice.dto.stage;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StageDto {
    private Long stageId;
    private String stageName;
    private Long projectId;
    private List<StageRolesDto> stageRolesDto;
}
