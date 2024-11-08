package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;

import java.time.LocalDateTime;

public record ProjectResp(
        Long id,
        String name,
        String description,
        Long ownerId,
        LocalDateTime createdAt,
        ProjectStatus status,
        ProjectVisibility visibility) {
}
