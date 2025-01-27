package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.model.Project;

public class SubProjectNameFilter implements SubProjectFilter {
    @Override
    public boolean isApplicable(SubProjectFilterDto filters) {
        return filters.getName() != null;
    }

    @Override
    public boolean filterEntity(Project project, SubProjectFilterDto filters) {
        return project.getName().contains(filters.getName());
    }
}
