package faang.school.projectservice.dto.Project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProjectDto {
    private Long id;

    @NotBlank(message = "Project name must not be empty.")
    private String name;

    @NotBlank(message = "Description name must not be empty.")
    private String description;

    @NotBlank(message = "OwnerId is required.")
    @Positive(message = "OwnerId must be greater than 0.")
    private Long ownerId;
    private ProjectVisibility visibility;
    private ProjectStatus status;
    private LocalDateTime updatedAt;
}
