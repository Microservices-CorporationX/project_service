package faang.school.projectservice.service.filters;

import faang.school.projectservice.dto.subprojectDto.subprojectFilterDto.SubprojectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface SubprojectFilter {
    boolean isApplicable(SubprojectFilterDto filters);
    Stream<Project> apply(Stream<Project> projects, SubprojectFilterDto filters);
}
