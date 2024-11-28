package faang.school.projectservice.dto.project;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskDto(
        @Nullable Long id,
        @NotBlank String name,
        @NotNull TaskStatusDto status,
        @NotNull Long projectId,
        @NotNull Long performerUserId,
        @NotNull Long reporterUserId
) {

}
