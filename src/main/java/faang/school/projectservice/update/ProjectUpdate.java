package faang.school.projectservice.update;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;


public interface ProjectUpdate {

    boolean isApplicable(ProjectDto projectDto);

    void apply(Project project, ProjectDto projectDto);
}
