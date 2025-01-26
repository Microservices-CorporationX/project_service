package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Builder;

@Builder
public record ProjectFilterDto(
        String name,
        ProjectStatus status
) {
}
