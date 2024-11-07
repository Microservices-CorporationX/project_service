package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class ProjectDto {

    @Positive(message = "Id must be greater than 0.")
    private Long id;

    @NotBlank(message = "Project name must not be empty.")
    @Length(min = 3, max = 128, message = "Name must be between 3 and 128 characters.")
    private String name;

    @NotBlank(message = "Project description must not be empty.")
    @Length(min = 10, max = 4096, message = "Description must be between 10 and 4096 characters.")
    private String description;

    @NotNull(message = "OwnerId is required.")
    @Positive(message = "OwnerId must be greater than 0.")
    private Long ownerId;

    private ProjectVisibility visibility;
    private ProjectStatus status;
}
