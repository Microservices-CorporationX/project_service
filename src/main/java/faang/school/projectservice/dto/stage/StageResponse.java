package faang.school.projectservice.dto.stage;

import lombok.Builder;

import java.util.List;

@Builder
public record StageResponse(
        Long stageId,
        String stageName,
        Long projectId,
        List<StageRolesDto> stageRolesDto
) {
}
