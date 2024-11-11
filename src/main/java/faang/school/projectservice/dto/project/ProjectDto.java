package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {

    private Long id;

    @NotNull
    private Long ownerId;

    @NotNull
    private Long parentId;

    @NotBlank
    @Size(max = 128)
    private String name;

    @NotBlank
    @Size(max = 4096)
    private String description;

    @NotNull
    private ProjectStatus status;

    @NotNull
    private ProjectVisibility visibility;
}
