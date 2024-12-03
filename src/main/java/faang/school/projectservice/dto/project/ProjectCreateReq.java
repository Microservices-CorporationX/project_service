package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectCreateReq(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull ProjectVisibility visibility,
        @Min(1) Long ownerId) {
}
