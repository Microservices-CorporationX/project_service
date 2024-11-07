package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public class NameFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto projectFilterDto) {
        return projectFilterDto.getName() != null;
    }

    @Override
    public Stream<Project> apply(ProjectFilterDto projectFilterDto, Stream<Project> projects) {
        return projects.filter(project -> project.getName().contains(projectFilterDto.getName()));
    }
}
