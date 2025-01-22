package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.filter.MomentFilter;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectIdFilter implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto filter) {
        return filter.getProjectIdPattern() > 0;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> elements, MomentFilterDto filter) {
        return elements.filter(moment -> moment.getProjects().stream()
                .anyMatch(project -> project.getId().equals(filter.getProjectIdPattern())));
    }
}