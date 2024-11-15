package faang.school.projectservice.dto.stage;

import lombok.Data;

import java.util.List;

@Data
public class StageFilterDto {

    private List<StageRolesDto> stageRolesDto;

    private String stageName;
}
