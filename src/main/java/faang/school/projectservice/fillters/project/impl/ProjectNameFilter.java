package faang.school.projectservice.fillters.project.impl;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.fillters.project.ProjectFilter;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectNameFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto filters) {
        return filters.getNamePattern() != null;
    }

    @Override
    public List<Project> apply(List<Project> projects, ProjectFilterDto filters) {
        return projects.stream()
                .filter(project -> project.getName().contains(filters.getNamePattern()))
                .toList();
    }
}
