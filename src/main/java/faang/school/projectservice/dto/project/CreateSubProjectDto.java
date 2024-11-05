package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSubProjectDto {
    private Long id;
    private String name;
    private Project parentProject;
    private ProjectStatus status;
    private ProjectVisibility visibility;
}
