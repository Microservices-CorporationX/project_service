package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateSubProjectDto {
    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Long parentProjectId;

    @NotNull
    private ProjectVisibility visibility;
}