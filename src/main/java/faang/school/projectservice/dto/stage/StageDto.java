package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Collections;
import java.util.List;

public record StageDto(
        @PositiveOrZero Long stageId,
        @NotBlank String stageName,
        @PositiveOrZero Long projectId,
        List<StageRolesDto> stageRolesDto
) {
    public StageDto{
        stageRolesDto = stageRolesDto != null ? stageRolesDto : Collections.emptyList();
    }
}
