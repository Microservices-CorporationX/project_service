package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectNameFilter implements ProjectFilter {

    @Override
    public boolean isApplicable(ProjectFilterDto dto) {
        return dto.getNamePattern() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto dto) {
        return projects.filter(e -> e.getName().toLowerCase().contains(dto.getNamePattern().toLowerCase()));
    }
}
