package faang.school.projectservice.update;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;


public interface ProjectUpdate {

    boolean isApplicable(ProjectDto projectDto);

    void apply(Project project, ProjectDto projectDto);
}
