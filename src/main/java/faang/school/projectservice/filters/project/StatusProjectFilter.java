package faang.school.projectservice.filters.project;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StatusProjectFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projectStream, ProjectFilterDto filters) {
        return projectStream.filter(project -> project.getStatus().equals(filters.getStatus()));
    }
}
