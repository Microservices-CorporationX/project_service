package faang.school.projectservice.dto.client.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectDto {
    private Long id;
    @NotNull(message = "Project name is required")
    private String name;
    private String description;
    @NotNull(message = "Project status is required")
    private ProjectStatus status = ProjectStatus.CREATED;
    @NotNull(message = "Project visibility is required")
    private ProjectVisibility visibility = ProjectVisibility.PUBLIC;
    @NotNull(message = "Owner ID is required")
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
