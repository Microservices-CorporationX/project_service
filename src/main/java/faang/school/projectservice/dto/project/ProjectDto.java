package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ProjectDto(
        Long id,
        String name,
        String description,
        Long ownerId,
        Long parentProjectId,
        ProjectStatus status,
        ProjectVisibility visibility,
        List<ProjectDto> children,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}

