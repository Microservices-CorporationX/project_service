package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Collections;
import java.util.List;

@Builder
public record UpdateStageRequest(
        Long stageId,
        @NotBlank String stageName,
        @NotNull Long projectId,
        List<String> requiredRoles,
        List<Long> executorsIds
) {
    public UpdateStageRequest {
        requiredRoles = requiredRoles != null ? requiredRoles : Collections.emptyList();
        executorsIds = executorsIds != null ? executorsIds : Collections.emptyList();
    }
}
