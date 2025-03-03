package ru.corporationx.projectservice.model.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.corporationx.projectservice.model.entity.ProjectStatus;
import ru.corporationx.projectservice.model.entity.ProjectVisibility;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProjectDto {
    private Long id;
    @NotNull(message = "Project name must not be null")
    @NotBlank(message = "Project name must not be blank")
    private String name;
    private List<@NotNull Long> childrenIds;
    @NotNull(message = "Project description must not be null")
    @NotBlank(message = "Project description must not be blank")
    private String description;
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectStatus status;
    @NotNull(message = "Project visibility must not be null")
    private ProjectVisibility visibility;
}
