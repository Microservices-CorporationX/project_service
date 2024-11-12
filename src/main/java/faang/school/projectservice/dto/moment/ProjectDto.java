package faang.school.projectservice.dto.moment;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectDto {
    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectStatus status;
}
