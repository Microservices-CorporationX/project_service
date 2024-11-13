package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.StageRoles;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;

@Data
public class StageDto {

    @Min(value = 1, message = "stageId должен быть больше 0")
    private Long stageId;

    private List<StageRoles> stageRoles;

    private List<Integer> count;

    @Data
    public static class StageRolesDto {
        private Long id;
        private TeamRole teamRole;
        private Integer count;
    }
}
