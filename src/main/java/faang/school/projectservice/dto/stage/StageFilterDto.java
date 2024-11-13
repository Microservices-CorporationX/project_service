package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.stage.StageRoles;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class StageFilterDto {
    private List<StageRoles> stageRoles;
}
