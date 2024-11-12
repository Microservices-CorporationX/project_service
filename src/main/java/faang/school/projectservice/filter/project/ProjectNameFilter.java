package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;

public class ProjectNameFilter implements ProjectFilter {
    @Override
    public boolean apply(Project project, ProjectFilterDto filter) {
        return filter.getName() == null || (project.getName() != null && project.getName().matches(filter.getName()));
    }
}
