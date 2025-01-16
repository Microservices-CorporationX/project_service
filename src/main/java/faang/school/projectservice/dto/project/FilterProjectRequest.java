package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Builder;

/**
 * DTO for {@link faang.school.projectservice.model.Project}
 */
@Builder
public record FilterProjectRequest(String name, ProjectStatus status) {
}