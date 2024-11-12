package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface SubProjectFilter {

    boolean isApplicable(SubProjectFilterDto filters);

    Stream<Project> apply(Stream<Project> projects, SubProjectFilterDto filters);
}
