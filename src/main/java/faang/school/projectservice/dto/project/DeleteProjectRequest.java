package faang.school.projectservice.dto.project;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

/**
 * DTO for {@link faang.school.projectservice.model.Project}
 */
@Builder
public record DeleteProjectRequest(@NotNull @Positive Long id,
                                   @NotNull @Positive Long ownerId) {
}