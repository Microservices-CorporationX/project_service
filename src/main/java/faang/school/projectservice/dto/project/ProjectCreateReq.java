package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectCreateReq {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private ProjectVisibility visibility;
    @Min(1)
    private Long ownerId;
}
