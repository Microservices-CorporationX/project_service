package faang.school.projectservice.fillters.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.List;

public interface ProjectFilter {

    boolean isApplicable(ProjectFilterDto filters);

    List<Project> apply(List<Project> projects, ProjectFilterDto filters);
}
