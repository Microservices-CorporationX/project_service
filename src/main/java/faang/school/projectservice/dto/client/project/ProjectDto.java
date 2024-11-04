package faang.school.projectservice.dto.client.project;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectDto {
    private Long id;
    @NotNull(message = "Project name is required")
    private String name;
    private String description;
    private String status;
    private String visibility;
    @NotNull(message = "Owner ID is required")
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
