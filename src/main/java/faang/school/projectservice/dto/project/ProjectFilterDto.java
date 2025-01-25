package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectFilterDto {

    @NotBlank(message = "Project name must not be empty.")
    private String name;

    private ProjectStatus status;
}
