package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Data;

@Data
public class ProjectFilterDto {
    private String namePattern;
    private ProjectStatus projectStatus;
    private ProjectVisibility projectVisibility;
}
