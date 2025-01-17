package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

/**
 * DTO for {@link faang.school.projectservice.model.Project}
 */
@Builder
public record UpdateProjectRequest(@NotNull @Positive Long id,
                                   @NotEmpty String name,
                                   String description,
                                   Long ownerId,
                                   ProjectStatus status,
                                   ProjectVisibility visibility
) {
}