package faang.school.projectservice.filter;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface ProjectFilter {
    boolean isApplicable(ProjectFilterDto projectFilterDto);
    Stream<Project> apply(ProjectFilterDto projectFilterDto, Stream<Project> projects);
}
