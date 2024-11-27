package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubProjectDto {
    @Positive
    private Long id;
    @NotNull(message = "Project name must not be null")
    @NotBlank(message = "Project name must not be blank")
    private String name;
    @NotNull(message = "Project description must not be null")
    @NotBlank(message = "Project description must not be blank")
    private String description;
    @Positive
    @NotNull(message = "Parent id must not be null")
    private Long parentId;
    private List<@NotNull Long> childrenIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectVisibility visibility;
    private ProjectStatus status;
}
