package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectCreateResponseDto {
    private String name;
    private String description;
    private Long ownerId;
    private Long parentProjectId;
    private ProjectStatus status;
    private ProjectVisibility visibility;
    private LocalDateTime createdAt;
}
