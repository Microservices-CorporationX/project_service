package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;

public record FilterSubProjectDto(
        String name,
        ProjectStatus status,
        ProjectVisibility visibility) {
}
