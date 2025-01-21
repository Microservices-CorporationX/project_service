package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Builder;

/**
 * DTO for {@link faang.school.projectservice.model.Project}
 */
@Builder
public record ProjectResponse(Long id,
                              String name,
                              String description,
                              Long ownerId,
                              ProjectStatus status,
                              ProjectVisibility visibility) {
}