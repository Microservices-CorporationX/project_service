package faang.school.projectservice.service.filters;

import faang.school.projectservice.dto.project.FilterSubProjectDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class NameProjectFilter implements FilterProjects {

        @Override
        public boolean isApplicable(FilterSubProjectDto filters) {
            return filters.name() != null;
        }

        @Override
        public Stream<Project> apply(Stream<Project> subProjects, FilterSubProjectDto filters) {
            return subProjects
                    .filter(subs -> subs.getName().contains(filters.name()));
        }
}
