package faang.school.projectservice.dto.client.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private Long id;
    @NotNull(message = "Project name is required")
    private String name;
    private String description;
    @Builder.Default
    @NotNull(message = "Project status is required")
    private ProjectStatus status = ProjectStatus.CREATED;
    @Builder.Default
    @NotNull(message = "Project visibility is required")
    private ProjectVisibility visibility = ProjectVisibility.PUBLIC;
    @NotNull(message = "Owner ID is required")
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
