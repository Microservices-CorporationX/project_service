package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Data;

@Data
public class ProjectFiltersReq {
    private String name;
    private ProjectStatus status;
}
