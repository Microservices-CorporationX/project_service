package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDto {

    @Positive(message = "Id must be greater than 0.")
    private Long id;

    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 128, message = "Project name must be between 3 and 128 characters")
    private String name;

    @NotBlank(message = "Project description must not be empty.")
    @Size(min = 10, max = 4096, message = "Description must be between 10 and 4096 characters.")
    private String description;

    @NotNull(message = "OwnerId is required.")
    @Positive(message = "OwnerId must be greater than 0.")
    private Long ownerId;

    @NotNull(message = "Visibility is required.")
    private ProjectVisibility visibility;
    private ProjectStatus status;
}
