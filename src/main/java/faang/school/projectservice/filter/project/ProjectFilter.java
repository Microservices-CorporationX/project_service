package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.List;

public interface ProjectFilter {
    boolean isApplicable(ProjectFilterDto projectFilterDto);

    void apply(List<Project> projectStream, ProjectFilterDto projectFilterDto);
}
