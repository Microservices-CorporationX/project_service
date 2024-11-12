package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;

public class ProjectStatusFilter implements ProjectFilter{
    @Override
    public boolean apply(Project project, ProjectFilterDto filter) {
        return filter.getStatus() == null || (project.getStatus() != null && project.getStatus().equals(filter.getStatus()));
    }
}
