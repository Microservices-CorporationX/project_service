package faang.school.projectservice.dto.filter;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectFilterDto {
    private String name;
    private ProjectStatus status;
}
