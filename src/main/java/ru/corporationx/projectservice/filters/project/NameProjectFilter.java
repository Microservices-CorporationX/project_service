package ru.corporationx.projectservice.filters.project;

import org.springframework.stereotype.Component;
import ru.corporationx.projectservice.model.dto.filter.ProjectFilterDto;
import ru.corporationx.projectservice.model.entity.Project;

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
