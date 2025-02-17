package ru.corporationx.projectservice.filters.project;

import ru.corporationx.projectservice.model.dto.filter.ProjectFilterDto;
import ru.corporationx.projectservice.model.entity.Project;

import java.util.stream.Stream;

public interface ProjectFilter {

    boolean isApplicable(ProjectFilterDto dto);

    Stream<Project> apply(Stream<Project> projects, ProjectFilterDto dto);
}
