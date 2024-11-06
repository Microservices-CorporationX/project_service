package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {

    private Long id;

    @NotEmpty
    @NotBlank
    @Size(min = 1, max = 128)
    private String name;

    private ProjectStatus status;

    private ProjectVisibility visibility;

    @NotEmpty
    @NotBlank
    @Size(min = 1, max = 4096)
    private String description;

    @Positive
    private Long ownerId;
}
