package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Builder;

import java.util.Collections;
import java.util.List;

@Builder
public record StageDto(
        Long stageId,
        @NotBlank String stageName,
        @NotNull Long projectId,
        List<StageRolesDto> stageRolesDto
) {
    public StageDto {
        stageRolesDto = stageRolesDto != null ? stageRolesDto : Collections.emptyList();
    }
}
