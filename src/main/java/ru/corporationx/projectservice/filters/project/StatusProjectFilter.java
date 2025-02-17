package ru.corporationx.projectservice.filters.project;

import org.springframework.stereotype.Component;
import ru.corporationx.projectservice.model.dto.filter.ProjectFilterDto;
import ru.corporationx.projectservice.model.entity.Project;

import java.util.stream.Stream;

@Component
public class StatusProjectFilter implements ProjectFilter {

    @Override
    public boolean isApplicable(ProjectFilterDto dto) {
        return dto.getStatusPattern() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto dto) {
        return projects.filter(e -> e.getStatus().equals(dto.getStatusPattern()));
    }
}
