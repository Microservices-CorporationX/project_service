package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectDto {
    private Long id;

    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank")
    @Size(max = 128, message = "Name must not exceed 128 characters")
    private String name;

    @NotNull(message = "Description must not be null")
    @Size(max = 4096, message = "Description must not exceed 4096 characters")
    @NotBlank(message = "Description must not be blank")
    private String description;

    @NotNull(message = "OwnerId must not be null")
    @Min(value = 0, message = "OwnerId must be non-negative")
    private Long ownerId;

    @Min(value = 0, message = "ParentProjectId must be non-negative")
    private Long parentProjectId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private ProjectStatus status;

    @NotNull(message = "Visibility must not be null")
    private ProjectVisibility visibility;

    @Min(value = 0, message = "СoverImageId must be non-negative")
    private String coverImageId;

    private List<Long> teamsIds;

    private List<Long> childrenIds;
}
