package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.List;

public interface ProjectFilter {
    boolean apply(Project project, ProjectFilterDto filter);

    static boolean applyAll(List<ProjectFilter> filters, Project project, ProjectFilterDto filterDto) {
        for (ProjectFilter filter : filters) {
            if (filter.apply(project, filterDto)) {
                return false;
            }
        }
        return true;
    }
}
