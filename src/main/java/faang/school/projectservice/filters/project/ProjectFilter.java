package faang.school.projectservice.filters.project;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface ProjectFilter {

    boolean isApplicable(ProjectFilterDto dto);

    Stream<Project> apply(Stream<Project> projects, ProjectFilterDto dto);
}
