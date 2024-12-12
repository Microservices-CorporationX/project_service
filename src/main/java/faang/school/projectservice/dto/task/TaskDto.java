package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record TaskDto(
        Long id,
        @NotBlank String name,
        String description,
        TaskStatus status,
        Long parentTaskId,
        Long stageId,
        @NotNull @Positive Long performerUserId,
        @NotNull @Positive Long projectId
) {
}
