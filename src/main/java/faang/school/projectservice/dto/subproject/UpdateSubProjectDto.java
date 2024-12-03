package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Builder;

@Builder
public record UpdateSubProjectDto(
        ProjectStatus status,
        ProjectVisibility visibility
) {
}
