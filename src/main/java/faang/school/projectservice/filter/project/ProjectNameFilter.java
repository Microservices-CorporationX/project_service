package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public class ProjectNameFilter implements Filter<Project, ProjectFilterDto> {

    @Override
    public boolean isApplicable(ProjectFilterDto filter) {
        return filter.getNameProjectPattern() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projectStream, ProjectFilterDto filter) {
        return projectStream.filter(project -> project
                .getName()
                .toLowerCase()
                .contains(filter.getNameProjectPattern().toLowerCase()));
    }
}
