package faang.school.projectservice.dto.client.subprojectdto;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CreateSubProjectDto {
    private String name;
    private String description;
    private ProjectStatus status;
    private ProjectVisibility visibility;
    private Long parentProjectId;
}
