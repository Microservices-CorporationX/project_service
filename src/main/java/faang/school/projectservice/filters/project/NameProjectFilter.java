package faang.school.projectservice.filters.project;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class NameProjectFilter implements ProjectFilter {

    @Override
    public boolean isApplicable(ProjectFilterDto dto) {
        return dto.getNamePattern() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto dto) {
        return projects.filter(e -> e.getName().toLowerCase().contains(dto.getNamePattern().toLowerCase()));
    }
}
