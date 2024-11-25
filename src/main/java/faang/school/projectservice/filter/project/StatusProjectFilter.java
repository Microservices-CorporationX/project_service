package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StatusProjectFilter implements ProjectFilter{

    @Override
    public boolean isApplicable(ProjectFilterDto dto) {
        return dto.getStatusPattern() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto dto) {
        return projects.filter(e -> e.getStatus().equals(dto.getStatusPattern()));
    }
}
