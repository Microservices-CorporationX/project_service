package faang.school.projectservice.filter.projectfilter;

import faang.school.projectservice.dto.Project.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface ProjectFilter <T,K>{
    boolean isApplicable(ProjectFilterDto filterDto);

    Stream<Project> apply(Stream<Project> projects, ProjectFilterDto filterDto);

}
