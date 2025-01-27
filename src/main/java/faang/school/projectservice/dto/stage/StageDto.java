package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageDto {
    private Long stageId;
    @NotNull(message = "The stageName field cannot be null!")
    private String stageName;
    @NotNull(message = "The projectId field cannot be null!")
    private Long projectId;
    @NotNull(message = "The userId field cannot be null!")
    private Long userId;
    @NotNull(message = "The stageRoles field cannot be null!")
    private List<StageRolesDto> stageRoles;
}
