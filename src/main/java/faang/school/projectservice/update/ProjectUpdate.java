package faang.school.projectservice.update;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;

public interface ProjectUpdate {

    boolean isApplicable(ProjectDto projectDto);

    void apply(Project project, ProjectDto projectDto);
}
