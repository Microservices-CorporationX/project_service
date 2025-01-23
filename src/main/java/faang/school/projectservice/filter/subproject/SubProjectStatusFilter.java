package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.model.Project;

public class SubProjectStatusFilter implements SubProjectFilter {
    @Override
    public boolean isApplicable(SubProjectFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public boolean filterEntity(Project project, SubProjectFilterDto filters) {
        return project.getStatus() == filters.getStatus();
    }
}
