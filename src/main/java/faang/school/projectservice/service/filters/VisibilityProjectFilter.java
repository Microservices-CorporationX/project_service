package faang.school.projectservice.service.filters;

import faang.school.projectservice.dto.project.FilterSubProjectDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class VisibilityProjectFilter implements FilterProjects {

    @Override
    public boolean isApplicable(FilterSubProjectDto filters) {
        return filters.visibility() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> subProjects, FilterSubProjectDto filters) {
        return subProjects
                .filter(subs -> Objects.equals(subs.getVisibility(), filters.visibility()));
    }
}

