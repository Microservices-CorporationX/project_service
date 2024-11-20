package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StageDto {
    private Long stageId;
    private String stageName;
    @NotNull
    private Long projectId;
    private List<StageRolesDto> stageRolesDto;
}
