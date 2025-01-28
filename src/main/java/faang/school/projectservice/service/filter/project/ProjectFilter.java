package faang.school.projectservice.service.filter.project;

import faang.school.projectservice.dto.project.FilterProjectRequest;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface ProjectFilter {
    Stream<Project> filter(Stream<Project> stream, FilterProjectRequest request);
}
