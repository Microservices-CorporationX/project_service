package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StageDto {
    private Long stageId;
    @NotNull(groups = {Before.class})
    private String stageName;
    @NotNull(groups = {Before.class})
    private Long projectId;
    @NotNull(groups = {Before.class})
    private List<StageRolesDto> stageRoles;

    public interface After {}

    public interface Before {}
}
