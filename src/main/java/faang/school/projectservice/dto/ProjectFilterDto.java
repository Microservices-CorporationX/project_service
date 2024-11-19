package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectFilterDto {
    private String name;
    private ProjectStatus status;
}
