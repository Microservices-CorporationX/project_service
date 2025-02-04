package faang.school.projectservice.service.filters;

import faang.school.projectservice.dto.project.FilterSubProjectDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public interface FilterProjects {

        boolean isApplicable(FilterSubProjectDto filters);

        Stream<Project> apply(Stream<Project> subProjects, FilterSubProjectDto filters);
}
