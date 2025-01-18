package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ProjectDto {
    private long id;
    private String name;
    private long ownerId;
    private long parentProjectId;
    private ProjectStatus projectStatus;
    private ProjectVisibility projectVisibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
