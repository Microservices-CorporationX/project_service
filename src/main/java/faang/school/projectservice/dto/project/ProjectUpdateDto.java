package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProjectUpdateDto {
    private Long id;

    @NotBlank(message = "Project name must not be empty.")
    private String name;

    @NotBlank(message = "Description name must not be empty.")
    private String description;

    @NotNull
    private Long ownerId;
    private ProjectVisibility visibility;
    private ProjectStatus status;
    private LocalDateTime updatedAt;
}