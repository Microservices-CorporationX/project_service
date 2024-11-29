package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record ResponseProjectDto(
        @Positive long id,
        @NotBlank String name,
        @NotBlank String description,
        List<Long> teamIds,
        ProjectStatus statuses
) {
}
