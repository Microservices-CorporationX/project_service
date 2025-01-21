package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProjectResponseDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Long parentProjectId;
    private List<Long> childrenIds;
    private ProjectStatus status;
    private ProjectVisibility visibility;
    private LocalDateTime updatedAt;
}