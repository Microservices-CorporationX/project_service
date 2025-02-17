package ru.corporationx.projectservice.model.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.corporationx.projectservice.model.entity.ProjectStatus;
import ru.corporationx.projectservice.model.entity.ProjectVisibility;

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
    @Size(max = 255, message = "Project name should not exceed 255 characters")
    private String name;
    @NotNull(message = "Project description must not be null")
    @NotBlank(message = "Project description must not be blank")
    @Size(max = 255, message = "Project description should not exceed 255 characters")
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
