package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @Min(value = 1, message = "Project id must be greater than 0")
    private Long id;
    @NotNull(message = "Name cannot be null")
    @Size(min = 1, max = 128, message = "Name must be between 1 and 128 characters")
    private String name;
//    private Long parentId;
//    @NotNull(message = "Status cannot be null")
//    private ProjectStatus status;
    @NotNull(message = "Visibility cannot be null")
    private ProjectVisibility visibility;
}
