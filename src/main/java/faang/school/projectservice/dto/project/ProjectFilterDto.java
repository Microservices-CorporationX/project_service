package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectFilterDto {

    private String name;

    private String nameProjectPattern;

    private ProjectStatus status;
}
