package faang.school.projectservice.service.filter.project;

import faang.school.projectservice.dto.project.FilterProjectRequest;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class NameFilter implements ProjectFilter {
    @Override
    public Stream<Project> filter(Stream<Project> stream, FilterProjectRequest request) {
        return request.name() == null ? stream : stream.filter(project -> project.getName().equals(request.name()));
    }
}
