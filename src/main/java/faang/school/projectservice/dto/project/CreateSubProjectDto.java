package faang.school.projectservice.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSubProjectDto(
        @NotBlank String name,
        String description,
        @NotNull Long ownerId,
        @NotNull Long parentProjectId) {
}
