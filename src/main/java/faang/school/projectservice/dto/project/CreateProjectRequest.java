package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

/**
 * DTO for {@link faang.school.projectservice.model.Project}
 */
@Builder
public record CreateProjectRequest(@NotBlank String name,
                                   @NotBlank String description,
                                   Long ownerId,
                                   ProjectVisibility visibility,
                                   List<Long> teamIds) {
}