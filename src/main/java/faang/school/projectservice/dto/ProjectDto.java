package faang.school.projectservice.dto;

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
    private ProjectVisibility projectVisibility;
    private LocalDateTime createdAt;
}
