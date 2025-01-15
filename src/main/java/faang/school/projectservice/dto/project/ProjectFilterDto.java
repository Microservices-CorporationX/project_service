package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectFilterDto {
    private Long id;
    private Long ownerId;
    private String name;
    private ProjectStatus status;
}
