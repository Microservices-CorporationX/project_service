package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Data;

@Data
public class ProjectFilterDto {
    private String name;
    private ProjectStatus projectStatus;

}
