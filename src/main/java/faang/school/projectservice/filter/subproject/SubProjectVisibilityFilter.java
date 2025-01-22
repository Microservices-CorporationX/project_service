package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;

public class SubProjectVisibilityFilter implements SubProjectFilter {
    @Override
    public boolean isApplicable(SubProjectFilterDto filters) {
        return true;
    }

    @Override
    public boolean filterEntity(Project project, SubProjectFilterDto filters) {
        return project.getVisibility() == ProjectVisibility.PUBLIC;
    }
}
