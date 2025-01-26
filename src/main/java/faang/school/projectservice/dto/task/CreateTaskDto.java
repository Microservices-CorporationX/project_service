package faang.school.projectservice.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record CreateTaskDto(
    @NotBlank String name,
    @NonNull @Positive Long performerUserId,
    @NonNull @Positive Long reporterUserId,
    @NotBlank Long projectId
) {}
