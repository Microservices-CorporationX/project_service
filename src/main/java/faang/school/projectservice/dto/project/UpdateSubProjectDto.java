package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotNull;

public record UpdateSubProjectDto(
        @NotNull Long id,
        ProjectStatus status,
        ProjectVisibility visibility) {
}
