package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {

    private Long id;
    private Long ownerId;
    private Long parentId;
    private String name;
    private String description;
    private ProjectVisibility visibility;
}
